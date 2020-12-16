package streams

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

import crawlers.downloader.{ChromeHeadlessDownloader, Downloader}
import crawlers.dto.{Page, RequestSetting, RequestUri}
import crawlers.pipeline.{ConsolePipeline, FileDTO, FilePipeline, Pipeline}

import scala.concurrent.duration._
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class QunarPageProcessor extends TestCrawlerTrait{

  override def targetRequests: List[RequestUri] = {
    println("QUNARPAGEPROCESSOR TARGETREQUESTS")
    List(
    RequestUri("https://flight.qunar.com/site/oneway_list.htm?searchDepartureAirport=%E5%8C%97%E4%BA%AC&searchArrivalAirport=%E6%88%90%E9%83%BD&searchDepartureTime=2020-01-11&searchArrivalTime=2020-01-15&nextNDays=0&startSearch=true&fromCode=BJS&toCode=CTU&from=qunarindex&lowestPrice=null")
  )}

  override def requestSetting: RequestSetting = {
    println("QUNARPAGEPROCESSOR REQUESTSETTING")
    RequestSetting(
    domain = "flight.qunar.com",
    sleepTime = 1 seconds,
    xhrRequests = Set("https://flight.qunar.com/touch/api/domestic/wbdflightlist")
  )}

  override protected def doProcess(page: Page): Unit = {
    println("QUNARPAGEPROCESSOR PAGE ==> " + page)
    println("QUNARPAGEPROCESSOR RESULTITEMS SIZE ==>" + page.resultItems.size())
    println("QUNARPAGEPROCESSOR PAGE URL ==> " + page.url)
    println("QUNARPAGEPROCESSOR BODY ==> ")
    println("QUNARPAGEPROCESSOR PAGE TITLE ==> "+page.title)
    println("QUNARPAGEPROCESSOR DOWNLOADER => "+downloader)

    page.hrefs.foreach(println)
    val im= page.dom("img[src~=(?i)\\\\.(png|jpe?g|gif)]")
    val divss =  page.div("div.q_header_logo")

    println("== XXXXX ==" + divss.first())
    //val append_timstamp = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm").format(LocalDateTime.now)

    val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss-SS'Z'");
    val  dateSelected = formatter.format(new Date());
   // java.time.LocalDateTime.now.toString

    val result = FileDTO(fileName = s"QunarCrawlResults${dateSelected}",content = page.body.toString)
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
// Some("0 0/30 * * * ?")



}
