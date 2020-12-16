package crawlers.pipeline

import org.slf4j.{Logger, LoggerFactory}
import akka.actor.Actor
import crawlers.dto.PipelineEvent

class PipelineActorReceiver extends Actor {

  private lazy val logger: Logger = LoggerFactory.getLogger(classOf[PipelineActorReceiver])

  override def receive: Receive = {
    case pipeLineEvent: PipelineEvent[_] =>
      println("======= INSIDE PIPELINE ACTOR RECEIVER ======")
      val pipelineList = if (logger.isTraceEnabled()) {
        pipeLineEvent.pipelineList + ConsolePipeline
      } else {
        pipeLineEvent.pipelineList
      }

      pipelineList.foreach(println)
      pipelineList.foreach{ _.process(pipeLineEvent.pageResultItems)}
    case other =>
      println("======= INSIDE PIPELINE ACTOR RECEIVER OTHER ======")
      logger.warn(s"${this.getClass.getSimpleName} received wrong msg ${other}")
  }
}
