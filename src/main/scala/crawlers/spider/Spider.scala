package crawlers.spider

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}

import akka.actor.{ActorRef, PoisonPill, Props}
import crawlers.actor.ActorManager
import crawlers.downloader.DownloaderActorReceiver
import crawlers.dto.DownloadEvent
import crawlers.processor.PageProcessor
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

case class Spider(pageProcessor: PageProcessor) {

  private lazy val logger           =  LoggerFactory.getLogger(classOf[Spider])

  private var downloaderActorPath             = ""

  val running: AtomicBoolean        = new AtomicBoolean(false)

  val name: String                  = pageProcessor.name

  def start(): Unit =
    if (!running.getAndSet(true)) {
      //      if (pageProcessor.downloader.isInstanceOf[ChromeHeadlessDownloader.type] && ChromeHeadlessConfig.chromeDriverNotExecutable) {
      //        throw new IllegalStateException("""
      //            |cant find chrome driver to execute.
      //            |choose one chrome driver from https://npm.taobao.org/mirrors/chromedriver/70.0.3538.16/ to download and install into your system
      //          """.stripMargin)
      //      }

      println("SPIDER START")
      val downloaderActor: ActorRef = getDownloadActor
      execute(downloaderActor)
      SpiderPool.addSpider(this)
    }

  private def getDownloadActor: ActorRef = {
    println("GET DOWNLOADER ACTOR")
    downloaderActorPath = s"downloader-${name}-${System.currentTimeMillis()}"
    val downloaderActor = ActorManager.getNewSystemActor(
      "downloader-dispatcher",
      downloaderActorPath,
      props = Props[DownloaderActorReceiver]
    )
    println("DOWNLOADER ACTOR ----> "+ downloaderActor)
    downloaderActor
  }

  def restart(): Unit = {
    if (running.get()) {
      this.stop()
    }

    start()
  }

  def stop(): Unit =
    if(running.getAndSet(false)) {
      println("=== inside spider stop ====")
      ActorManager.getExistedActor(downloaderActorPath) ! PoisonPill
      SpiderPool.removeSpider(this)
      this.CrawlMetric.clean()
    }

  private def execute(downLoaderActor: ActorRef): Future[Unit] =
    Future {
      println("SPIDER EXECUTE")
      this.pageProcessor.targetRequests.foreach{ url =>
        println("SPIDER EXECUTE FOR EACH")
        downLoaderActor ! DownloadEvent(spider = this, request = pageProcessor.requestSetting.withRequestUri(url))
        TimeUnit.MILLISECONDS.sleep(this.pageProcessor.requestSetting.sleepTime.toMillis)

      }
    }


  object CrawlMetric {
    private val downloadPageSuccessNum     = new AtomicInteger(0)
    private val downloadPageFailedNum      = new AtomicInteger(0)
    private val processPageSuccessNum      = new AtomicInteger(0)

    def downloadedPageSum: Int = downloadPageSuccessNum.get() - downloadPageFailedNum.get()

    def downloadSuccessCounter  = downloadPageSuccessNum.getAndIncrement()

    def downloadFailedCounter: Int   = downloadPageFailedNum.getAndIncrement()

    def processedSuccessCounter: Int = processPageSuccessNum.getAndIncrement()

    def clean(): Unit = {
      downloadPageSuccessNum.set(0)
      downloadPageFailedNum.set(0)
      processPageSuccessNum.set(0)
    }

    def record(success: Boolean, uri: String): Unit = {
      if(logger.isDebugEnabled())
        logger.debug(s"downloaded: ${success} ${uri}")
      if(success) downloadSuccessCounter
      else downloadFailedCounter
    }
  }

  override def toString: String = s"spider-${name}: ${CrawlMetric.downloadedPageSum}"
}
