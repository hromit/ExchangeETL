package crawlers.downloader.proxy.crawler
import crawlers.downloader.proxy.ProxyDTO
import crawlers.dto.{Page, RequestSetting}

import scala.concurrent.duration._

import scala.util.Try

case class IP89Processor() extends ProxyProcessorTrait {
  override protected def doProcess(page: Page): Unit = {
    val iprows = page.table("tbody tr")
    iprows.foreach{ ip =>
      val tds      = ip.select("td")
      val proxyDto = ProxyDTO(host = tds.get(0).text(), port = Try(tds.get(1).text().toInt).getOrElse(80))
      page.addPageResultItem(proxyDto)
    }
  }

  override def requestSetting: RequestSetting = RequestSetting(domain = "www.89ip.com", sleepTime = 2 seconds)

  override def targetUrls: List[String] = List("http://www.89ip.cn")

}

