package crawlers.downloader.proxy.crawler

import crawlers.downloader.proxy.ProxyCrawlerPipeline
import crawlers.pipeline.Pipeline
import crawlers.processor.PageProcessor





trait ProxyProcessorTrait extends PageProcessor{
  override def cronExpression: Option[String] = Some("*/5 * * ? * *")

 override val pipelines: Set[Pipeline] = Set(ProxyCrawlerPipeline)

}
