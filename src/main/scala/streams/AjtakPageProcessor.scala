package streams

import crawlers.downloader.{ChromeHeadlessDownloader, Downloader}
import crawlers.dto.{Page, RequestSetting, RequestUri}

import scala.concurrent.duration._

class AjtakPageProcessor extends TestCrawlerTrait{

  override def targetRequests: List[RequestUri] = {
    println("=== inside qunar target req")
    List(
      RequestUri("https://aajtak.intoday.in/")
    )}

  override def requestSetting: RequestSetting = {
    println("=== inside qunar request setting")
    RequestSetting(
      domain = "flight.qunar.com",
      sleepTime = 1 seconds,
      xhrRequests = Set("https://aajtak.intoday.in/")
    )}

  override protected def doProcess(page: Page): Unit = {
    println("=== inside qunar do process" + page)
    println("=== inside qunar do process" + page.resultItems.size())
    println("qunar do process page url " + page.url)
    println("qunar do process page body ")
    println("qunar do process page title "+page.title)
    println("qunar do process page title "+downloader)
    println("qunar do process page title ")

    page.hrefs.foreach{ s  => if (s.mkString.contains("coronavirus")) println(s)}

   // page.hrefs.foreach{ println}



    val im= page.dom("img[src~=(?i)\\\\.(png|jpe?g|gif)]")
  //  val divss =  page.div("div.td-excerpt")

   // println("== XXXXX ==" + divss.first().text())

    /*
        import scala.collection.JavaConversions._
        for (element <- divss) {
          println("==== divsss ==========")
          println(element.ownText())
        }*/

    // println("qunar do process page body "+page.body)
    page.xhrResponses.foreach { response =>
      println(response.result)
    }
  }


  override val downloader: Downloader[_] = ChromeHeadlessDownloader


  override def cronExpression: Option[String] = Some("0 */1 * ? * *")


}

