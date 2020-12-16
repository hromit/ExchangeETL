package crawlers.downloader.proxy


import crawlers.pipeline.Pipeline

object ProxyCrawlerPipeline extends Pipeline{
  override def process[Result](pageResultItem: (String, Result)): Unit = {
    val (url, result) = pageResultItem

    val resultMap = result.asInstanceOf[Proxy]

  }

}
