package crawlers.downloader

import crawlers.downloader.proxy.ProxyDTO
import crawlers.dto.{Page, RequestSetting}
import crawlers.exceptions.IllegalArgumentsException
import io.netty.handler.codec.http.{DefaultHttpHeaders, HttpHeaderNames}
import org.asynchttpclient._
import org.asynchttpclient.Dsl.asyncHttpClient
import org.asynchttpclient.proxy.ProxyServer

import scala.concurrent.{Future, Promise, TimeoutException}

object AsyncHttpClientDownloader extends Downloader[AsyncHttpClient] {

  private[this] def buildRequest(driver: AsyncHttpClient, request: RequestSetting, proxyOpt: Option[ProxyDTO] = None): BoundRequestBuilder = {
    proxyOpt.foreach{ proxy =>
      buildProxy(proxy)(p => new ProxyServer.Builder(p.host, p.port).build())
    }

   val builder = builderMethod(driver, request)

   val httpHeaders = new DefaultHttpHeaders
   request.headers.foreach{ case (k,v) => httpHeaders.add(k,v) }
   httpHeaders.add(HttpHeaderNames.USER_AGENT, request.userAgent)
   httpHeaders.add(HttpHeaderNames.ACCEPT_CHARSET, request.charset)
   builder.setHeaders(httpHeaders)
  }


  def builderMethod(driver: AsyncHttpClient, requestSetting: RequestSetting): BoundRequestBuilder = {
    val url = requestSetting.url.get
    requestSetting.method.toUpperCase match {
      case "GET" =>
        driver.prepareGet(url)
      case "POST" =>
        val post = driver.preparePost(url)
        requestSetting.requestBody.foreach(post.setBody)
        post
      case other =>
        logger.warn(s"unknown hhtp method ${other}")
        throw IllegalArgumentsException(other)
    }
  }

  override protected def doDownload(request: RequestSetting): Future[Page] = {
    val response = executeRequest(request) { proxyOpt =>
      val promise = Promise[Response]

      val client = getOrCreateClient(request)
      buildRequest(client.driver, request, proxyOpt).execute(new AsyncCompletionHandler[Response]() {
        override def onCompleted(response: Response): Response = {
          promise.success(response)
          client.decrement()
          response
        }

        override def onThrowable(t: Throwable): Unit = {
          if(t.isInstanceOf[TimeoutException]) {
            logger.error("download error ", t)
          }
          client.decrement()
          promise.failure(t)
        }
      })
      promise.future
    }
    response.map{ r =>
      pageResult(request, Some(r.getResponseBodyAsBytes), r.getStatusCode == 200, Some(s"return ${r.getStatusCode}"))
    }(crawlers.actor.ExecutionContexts.downloadDispatcher)
  }

  override protected def closeClient(): Unit = closeDownloaderClient(_.close())

  override protected def getOrCreateClient(requestSetting: RequestSetting): DownloaderClient[AsyncHttpClient] =
    getDownloaderClient(requestSetting.domain) {
      asyncHttpClient(
        new DefaultAsyncHttpClientConfig.Builder()
         .setReadTimeout(requestSetting.timeOut.toMillis.toInt)
         .setConnectTimeout(requestSetting.timeOut.toMillis.toInt)
         .setFollowRedirect(true)
         .setConnectionPoolCleanerPeriod(5)
         .build()
      )
    }
}
