package crawlers.downloader

import akka.actor.{Actor, ActorRef, Props}
import crawlers.dto.{DownloadEvent, ProcessorEvent}
import crawlers.processor.PageProcessorActorReceiver
import org.slf4j.{Logger, LoggerFactory}


class DownloaderActorReceiver extends Actor{

  private val logger: Logger = LoggerFactory.getLogger(classOf[DownloaderActorReceiver])

  lazy val processorActor: ActorRef = context.actorOf(
    Props[PageProcessorActorReceiver].withDispatcher("crawlers.processor-dispatcher")
  )

  override def receive: Receive = {
    case downloadEvent: DownloadEvent =>
    println("DOWNLOADACTORRECEIVER RECEIVE METHOD CASE 1")
    val spider = downloadEvent.spider

    import crawlers.actor.ExecutionContexts.downloadDispatcher
      spider.pageProcessor.downloader.download(spider, downloadEvent.request).foreach {
        case page if page.isDownloadSuccess ⇒
          println("DOWNLOADACTORRECEIVER RECEIVE METHOD AFTER DOWNLOAD CASE 1")
          processorActor ! ProcessorEvent(spider, page)
        case page =>
          println("DOWNLOADACTORRECEIVER RECEIVE METHOD AFTER DOWNLOAD CASE 1")
          logger.warn(s"page failed to download cause ${page.source}")
      }
    case other ⇒
      println("DOWNLOADACTORRECEIVER RECEIVE METHOD CASE OTHER")
      logger.warn(s"${self.path} received wrong msg ${other}")
  }


  override def postStop(): Unit =
    if(logger.isWarnEnabled())
      logger.warn(s"downloader-processor [${self.path}] stopped!")

  override def postRestart(reason: Throwable): Unit =
    if (logger.isWarnEnabled())
      logger.warn(s"downloader-processor restart! ${reason.getLocalizedMessage}")

}
