package crawlers.downloader

import java.util.concurrent.{ConcurrentHashMap, Executors, ScheduledFuture, TimeUnit}
import java.util.concurrent.atomic.AtomicInteger

import crawlers.downloader.proxy.{ProxyDTO, ProxyProvider}
import crawlers.dto
import crawlers.dto.{Page, RequestSetting}
import crawlers.spider.Spider
import utils.RetryUtils._
import org.slf4j.{Logger, LoggerFactory}
import utils.ConfigUtils

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConverters._


trait Downloader[Driver] {

  protected lazy val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private val downloadRetryException: Seq[String] = ConfigUtils.getSeq[String]("crawlers.download.retry.exception")

  protected val clientsPool = new ConcurrentHashMap[String, DownloaderClient[Driver]]()

  protected def doDownload(requestSetting: RequestSetting): Future[Page]

  protected def buildProxy[P](proxyDTO: ProxyDTO)(buildProxy: ProxyDTO => P): P = buildProxy(proxyDTO)

  protected def closeClient(): Unit

  protected def getOrCreateClient(requestSetting: RequestSetting): DownloaderClient[Driver]

  protected def getClient(domain: String): Option[DownloaderClient[Driver]] = Option(clientsPool.get(domain))

  /**
   * common schedule job to download client
   */

  val scheduleClose: ScheduledFuture[_] = Executors
    .newSingleThreadScheduledExecutor()
    .scheduleAtFixedRate(new Runnable {
      override def run(): Unit =
        try (closeClient())
        catch { case e: Throwable => logger.error("", e)}
    }, 3, 3, TimeUnit.MINUTES)


  def download(spider: Spider, request: RequestSetting): Future[Page] = {
    println("DOWNLOADER DOWNLOAD METHOD")
    import crawlers.actor.ExecutionContexts.downloadDispatcher
    futureRetryWhen(doDownload(requestSetting = request), retryTime = request.retryTime, RetryInfo(duration = request.sleepTime, downloadRetryException))
      .map { page =>
        println("DOWNLOADER DOWNLOAD METHOD WHERE WE SEE PAGE FIRST TIME")
        spider.CrawlMetric.record(page.isDownloadSuccess, page.url)
        page
      }
      .recover {
        case NonFatal(e) =>
          spider.CrawlMetric.record(success = false, request.url.get)
          throw e
      }
  }


  protected def executeRequest[HttpResponse](requestSetting: RequestSetting)(execute: Option[ProxyDTO] =>
    Future[HttpResponse]): Future[HttpResponse] =
    if(requestSetting.useProxy) {
      execute(ProxyProvider.getProxy)
    } else {
      execute(None)
    }

  protected def pageResult(requestSetting: RequestSetting, results: Option[Array[Byte]] = None,
  downloadSuccess: Boolean = true, msg: Option[String] = None): Page =
    dto.Page(downloadSuccess, bytes = results, requestSetting = requestSetting)

  protected def getDownloaderClient(domain: String)(driver: => Driver): DownloaderClient[Driver] = {
    println("DOWNLOADER getDownloaderClient  METHOD")
    val clientCache = Option(clientsPool.get(domain))

    val downloaderClient = clientCache.getOrElse {
      val downloaderClient = DownloaderClient(domain = domain, driver = driver)
      clientsPool.put(domain, downloaderClient)
      downloaderClient
    }
    downloaderClient.increment()
    downloaderClient
  }

  def closeDownloaderClient(close: Driver => Unit): Unit =
    for (e <- clientsPool.entrySet().asScala) {
      val (domain, downloaderClient) = (e.getKey, e.getValue)
      if (downloaderClient.idle()) {
        Try(close(downloaderClient.driver)) match  {
          case Success(_) =>
            logger.info(s"${domain} downloader driver[${downloaderClient.driver.getClass.getSimpleName}] has been closed")
          case Failure(exception) =>
           logger.error(s"${domain} downloader driver failed to close. ${exception.getLocalizedMessage}")
        }
      clientsPool.remove(domain)
      }
    }

    sys.addShutdownHook{
      try (closeClient())
      catch { case e: Throwable => logger.error("", e)}
    }


}


case class DownloaderClient[C](domain: String, driver: C, consumers: AtomicInteger = new AtomicInteger(0)) {
  def idle(): Boolean = consumers.get() == 0
  def increment(): Int = consumers.incrementAndGet()
  def decrement(): Int = consumers.decrementAndGet()
}
