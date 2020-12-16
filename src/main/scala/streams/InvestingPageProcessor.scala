package streams

import java.text.SimpleDateFormat
import java.util.Date

import crawlers.downloader.{ChromeHeadlessDownloader, Downloader}
import crawlers.dto.{Page, RequestSetting, RequestUri}
import crawlers.pipeline.{ConsolePipeline, FileDTO, FilePipeline, Pipeline}

import scala.concurrent.duration._

class InvestingPageProcessor extends TestCrawlerTrait{

  override def targetRequests: List[RequestUri] = {
    println("InvestingPageProcessor TARGETREQUESTS")
    List(
      RequestUri("https://in.investing.com/")
    )}

  override def requestSetting: RequestSetting = {
    println("InvestingPageProcessor REQUESTSETTING")
    RequestSetting(
      domain = "in.investing.com",
      sleepTime = 1 seconds,
      xhrRequests = Set("https://in.investing.com/")
    )}

  override protected def doProcess(page: Page): Unit = {
    println("InvestingPageProcessor PAGE ==> " + page)
    println("InvestingPageProcessor RESULTITEMS SIZE ==>" + page.resultItems.size())
    println("InvestingPageProcessor PAGE URL ==> " + page.url)
    println("InvestingPageProcessor BODY ==> ")
    println("InvestingPageProcessor PAGE TITLE ==> "+page.title)
    println("InvestingPageProcessor DOWNLOADER => "+downloader)

    page.hrefs.foreach(println)
    val im= page.dom("img[src~=(?i)\\\\.(png|jpe?g|gif)]")
    val divss =  page.div("div.q_header_logo")

    println("== XXXXX ==" + divss.first())
    //val append_timstamp = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm").format(LocalDateTime.now)

    val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss-SS'Z'");
    val  dateSelected = formatter.format(new Date());
    // java.time.LocalDateTime.now.toString

    val result = FileDTO(fileName = s"InvestingCrawlResults${dateSelected}",content = page.body.toString)
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
