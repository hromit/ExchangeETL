package crawlers.downloader.proxy.crawler
import crawlers.downloader.proxy.ProxyDTO
import crawlers.dto.{Page, RequestSetting}

import scala.concurrent.duration._

case class Data5UPageProcessor() extends ProxyProcessorTrait {

  override protected def doProcess(page: Page): Unit = {
    val ipRow  = page.dom(".wlist > ul > li:nth-child(2) .l2")
    val ipSize = ipRow.size()

    (0 until ipSize).foreach(i => {
      val ip   = ipRow.get(i).select("span:nth-child(1)").text()
      val port = ipRow.get(i).select("span:nth-child(1)").text()

      val proxy = ProxyDTO(ip, port.toInt)
      page.addPageResultItem(proxy)
    })
  }

  override def cronExpression: Option[String] = Some("*/5 * * ? * *")

  override def requestSetting: RequestSetting = RequestSetting(domain = "www.data5u.com", sleepTime = 2 seconds)

  override def targetUrls: List[String] = List("http://www.data5u.com")
}
