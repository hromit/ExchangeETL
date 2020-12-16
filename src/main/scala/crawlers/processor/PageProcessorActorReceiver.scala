package crawlers.processor

import java.util.concurrent.LinkedBlockingQueue

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import crawlers.dto.{DownloadEvent, PipelineEvent, ProcessorEvent}
import crawlers.pipeline.{Pipeline, PipelineActorReceiver}
import crawlers.queue.RequestQueue
import crawlers.spider.Spider
import crawlers.actor.ExecutionContexts.processorDispatcher
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Success, Failure}

class PageProcessorActorReceiver extends Actor{

  private lazy val logger: Logger = LoggerFactory.getLogger(classOf[PageProcessorActorReceiver])

  val pipelineActor: ActorRef = context.actorOf(Props[PipelineActorReceiver].withDispatcher("crawlers.pipeline-dispatcher"), "pipeline-processor")

  override def receive: Receive = {
    case processorEvent: ProcessorEvent =>
      println("PAGEPROCESSORACTORRECEIVER RECEIVE CASE PROCESSOR EVENT")
      val page           =  processorEvent.page
      val spider         =  processorEvent.spider
      val downloadSender =  sender()

      spider.pageProcessor.process(page).onComplete{
        case Success(_) =>
          println("PAGEPROCESSORACTORRECEIVER RECEIVE CASE SUCCESS")
          println("PAGEPROCESSORACTORRECEIVER RECEIVE CASE SUCCESS PRINTING PIPELINES SIZE => "+ spider.pageProcessor.pipelines.size)
          spider.pageProcessor.pipelines.foreach(println)
          println("PAGEPROCESSORACTORRECEIVER RECEIVE CASE SUCCESS PRINTING RESULTITEMS SIZE => "+ page.url)
          println("PAGEPROCESSORACTORRECEIVER RECEIVE CASE SUCCESS PRINTING RESULTITEMS SIZE => "+ page.isDownloadSuccess)
          println("PAGEPROCESSORACTORRECEIVER RECEIVE CASE SUCCESS PRINTING RESULTITEMS SIZE => "+ page.resultItems.size)


          pipelineProcess(page.requestSetting.url.get, page.resultItems)(spider.pageProcessor.pipelines)(downloadSender)
          spider.CrawlMetric.processedSuccessCounter

          continueRequest(page.resultQueue)(spider)(downloadSender)
        case Failure(value) =>
          println("PAGEPROCESSORACTORRECEIVER RECEIVE CASE FAILURE")
          logger.error(s"failed to process page ${page.url}")
      }

    case other =>
      println("PAGEPROCESSORACTORRECEIVER RECEIVE CASE OTHER")
      logger.warn(s"${self.path} received wrong msg ${other}")
  }

  private[this] def pipelineProcess(url: String, pageResultItems: LinkedBlockingQueue[Any])(pipelines: Set[Pipeline])(downLoadSender: ActorRef): Unit = {
    println("PAGEPROCESSORACTORRECEIVER  pipelineProcess METHOD")
    while (!pageResultItems.isEmpty) {
      println("PAGEPROCESSORACTORRECEIVER  pipelineProcess METHOD WHILE")
      Option(pageResultItems.poll()).foreach{ item =>
        PipelineEvent(pipelines, (url, item)).initPipelines() match {
          case Some(e) => pipelineActor ! e
          case None    => downLoadSender ! PoisonPill
        }
      }
    }
  }

  private[this] def continueRequest(targetRequests: RequestQueue)(spider: Spider)(downLoadSender: ActorRef): Unit =
    while (targetRequests.nonEmpty) {
      targetRequests.poll().foreach { targetRequest =>
        downLoadSender ! DownloadEvent(spider, targetRequest)
      }
    }
}
