package crawlers.pipeline

object ConsolePipeline extends Pipeline{
  override def process[Result](pageResultItem: (String, Result)): Unit = {
    val (url, result) = pageResultItem
    logger.trace(s"crawl result: ${url} - ${result}")
  }
}
