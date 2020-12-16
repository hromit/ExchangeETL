package crawlers.dto

import crawlers.spider.Spider
import crawlers.pipeline.Pipeline
import utils.logger.Logging

import scala.util.{Try,Success, Failure}

sealed trait Event

case class DownloadEvent(spider: Spider, request: RequestSetting) extends Event

case class ProcessorEvent(spider: Spider, page: Page) extends Event

case class PipelineEvent[R](pipelineList: Set[Pipeline], pageResultItems: (String, R)) extends Event with Logging {
  def initPipelines(): Option[PipelineEvent[R]] = {
    val allInited = pipelineList
      .map{ p =>
        Try(p.open()) match {
          case Success(_) => true
          case Failure(exception) =>
            logger.error(s"failed to init pipeline ${exception.getLocalizedMessage}")
            false
        }
      }
      .forall(_ == true)

    if (allInited) Some(this) else None

  }
}


