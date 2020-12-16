package crawlers.downloader.proxy.crawler
import crawlers.downloader.proxy.ProxyDTO
import crawlers.dto.{Page, RequestSetting}

import scala.concurrent.duration._

case class A2UPageProcessor() extends ProxyProcessorTrait {
  override protected def doProcess(page: Page): Unit = {
    val proxyIpList = page.body.text().split(" ")

    proxyIpList.foreach(it => {
      val ipAndPort = it.split(":")
      val proxy     = ProxyDTO(ipAndPort.head, ipAndPort.last.toInt)
      page.addPageResultItem(proxy)
    })
  }

  override def requestSetting: RequestSetting = RequestSetting(domain = "raw.githubusercontent.com", sleepTime = 2 seconds)

  override def targetUrls: List[String] =
    List(
      "https://raw.githubusercontent.com/a2u/free-proxy-list/master/free-proxy-list.txt"
    )
}
