package crawlers.dto

import java.net.URL
import java.util.concurrent.LinkedBlockingQueue

import crawlers.selector.HtmlParser
import crawlers.selector.HtmlParser.parseJson
import crawlers.queue.TargetRequestTaskQueue
import org.apache.hadoop.fs.FSExceptionMessages
import utils.logger.Logging

import scala.util.Try


case class Page(
     isDownloadSuccess: Boolean = true,
     bytes: Option[Array[Byte]] = None,
     responseHeaders: Map[String, String] = Map.empty[String, String],
     xhrResponses: Seq[XhrResponse] = Seq.empty[XhrResponse],
     requestSetting: RequestSetting){

  lazy val resultItems: LinkedBlockingQueue[Any] = new LinkedBlockingQueue[Any]
  lazy val resultQueue: TargetRequestTaskQueue = new TargetRequestTaskQueue()

  lazy val url = requestSetting.url.get

  def source: String = bytes match {
    case Some(byte) =>
      println("PAGE SOURCE METHOD")
      HtmlParser.getHtmlSourceWithCharset(byte, requestSetting.charset)
    case None =>
    throw new IllegalStateException("no page source text found")
  }

  def json[T: Manifest](text: Option[String]) = parseJson[T](text.getOrElse(this.source))

 def addTargetRequest(urlAdd: String): Unit = addRequest(this.requestSetting.withUrl(url = urlAdd ))

 def addTargetRequest(requestUri: RequestUri): Unit = addRequest(this.requestSetting.withRequestUri(requestUri))

  private[this] def addRequest(requestSetting: RequestSetting): Unit = {
    val url = requestSetting.url.get

    if (Try(new URL(url)).isSuccess) {
      this.resultQueue.push(requestSetting)
    }
  }

  def addPageResultItem[R](result: R): Unit = this.resultItems.add(result)

  override def toString: String = s"${requestSetting.url.get} downloaded ${isDownloadSuccess}"

}


object Page extends Logging {
  def failed(requestSetting: RequestSetting, exceptionMessages: Throwable): Page = {
    logger.warn(s"failed to download cause ${exceptionMessages.getLocalizedMessage}")
    Page(requestSetting = requestSetting, isDownloadSuccess = false)
  }
}




case class XhrResponse(xhrUri: String, result: Map[String,Any])
