package crawlers.processor

import crawlers.downloader.{AsyncHttpClientDownloader, Downloader}
import crawlers.dto.{Page, RequestSetting, RequestUri}
import crawlers.pipeline.{ConsolePipeline, Pipeline}
import crawlers.selector.HtmlParser

import crawlers.actor.ExecutionContexts.processorDispatcher

import scala.concurrent.Future

trait PageProcessor extends  HtmlParser{

  val name: String = this.getClass.getSimpleName

  /**
   * download client
   */

  val downloader: Downloader[_] = AsyncHttpClientDownloader

  /**
   * Target urls for processor to crawl
   */
  @deprecated
  def targetUrls: List[String] = Nil

  /**
   * the target request for processor to crawl
   * @return
   */
  def targetRequests: List[RequestUri] =
    if (targetUrls.nonEmpty) {
      targetUrls.map(url => RequestUri(url))
    }else {
      List.empty[RequestUri]
    }

  /**
   * handle the crawled result
   * @return
   */
  val pipelines: Set[Pipeline] = Set(ConsolePipeline)

  /**
   * parse the html source code
   * @param page
   */
  def process(page: Page): Future[Unit] = Future {
    doProcess(page)
  }

  protected  def doProcess(page: Page): Unit

  /**
   * set request config for processor
   */

  def requestSetting: RequestSetting = RequestSetting(url = None)

  /**
   * schedule cron job expression
   */

  def cronExpression: Option[String] = None
}
