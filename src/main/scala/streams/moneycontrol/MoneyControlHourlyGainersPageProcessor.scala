package streams.moneycontrol

import java.text.SimpleDateFormat
import java.util.Date

import crawlers.downloader.{ChromeHeadlessDownloader, Downloader}
import crawlers.dto.{Page, RequestSetting, RequestUri}
import crawlers.pipeline.{ConsolePipeline, FileDTO, FilePipeline, Pipeline}
import streams.TestCrawlerTrait

import scala.concurrent.duration._

class MoneyControlHourlyGainersPageProcessor  extends TestCrawlerTrait{

  /*
    Hourly Gainers has category apart from curr_hour(url)
    upto10 - url change hour_1
    10-11 -  url change hour_2
    11-12 -  url change hour_3
    12-1 -  url change hour_4
    1-2 -  url change hour_5
    2-3 -  url change hour_6
   */

  override def targetRequests: List[RequestUri] = {
    println("=== inside qunar target req")
    List(
      RequestUri("https://www.moneycontrol.com/stocks/marketstats/hourly_gain/nse/curr_hour/index.php")
    )}

  override def requestSetting: RequestSetting = {
    println("=== inside qunar request setting")
    RequestSetting(
      domain = "moneycontrol.com",
      sleepTime = 1 seconds,
      xhrRequests = Set("https://www.moneycontrol.com/stocks/marketstats/hourly_gain/nse/curr_hour/index.php")
    )}

  override protected def doProcess(page: Page): Unit = {
    println("NSEIndiaPageProcessor PAGE ==> " + page)
    println("NSEIndiaPageProcessor RESULTITEMS SIZE ==>" + page.resultItems.size())
    println("NSEIndiaPageProcessor PAGE URL ==> " + page.url)
    println("NSEIndiaPageProcessor BODY ==> ")
    println("NSEIndiaPageProcessor PAGE TITLE ==> "+page.title)
    println("NSEIndiaPageProcessor DOWNLOADER => "+downloader)

    page.hrefs.foreach(println)
    val im= page.dom("img[src~=(?i)\\\\.(png|jpe?g|gif)]")
    val divss =  page.div("div.q_header_logo")

    println("== XXXXX ==" + divss.first())
    //val append_timstamp = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm").format(LocalDateTime.now)

    val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss-SS'Z'");
    val  dateSelected = formatter.format(new Date());
    // java.time.LocalDateTime.now.toString

    import org.jsoup.Jsoup
    import org.jsoup.safety.Whitelist
    val cleanedHTML = Jsoup.clean(page.body.toString, Whitelist.relaxed())

    println(cleanedHTML)



    val result = FileDTO(fileName = s"MoneyControlHourlyGainersPageProcessor${dateSelected}",content = cleanedHTML.toString)
    page.addPageResultItem(result)

    // case class FileDTO(fileName: String, fileType: String = "html", content: String)

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

  override val pipelines: Set[Pipeline] = Set(FilePipeline(Some("C:\\Users\\91801\\Downloads\\chromedriver_win32")), ConsolePipeline)

  override val downloader: Downloader[_] = ChromeHeadlessDownloader

  override def cronExpression: Option[String] = Some("0 */2 * ? * *")


}

