package streams

import java.text.SimpleDateFormat
import java.util.Date

import crawlers.downloader.{ChromeHeadlessDownloader, Downloader}
import crawlers.dto.{Page, RequestSetting, RequestUri}
import crawlers.pipeline.{ConsolePipeline, FileDTO, FilePipeline, Pipeline}

import scala.concurrent.duration._

class MoneyControlPageProcessor extends TestCrawlerTrait{

  override def targetRequests: List[RequestUri] = {
    println("=== inside qunar target req")
    List(
      RequestUri("https://www.moneycontrol.com/stocks/marketstats/nsegainer/index.php")
    )}

  override def requestSetting: RequestSetting = {
    println("=== inside qunar request setting")
    RequestSetting(
      domain = "moneycontrol.com",
      sleepTime = 1 seconds,
      xhrRequests = Set("https://www.moneycontrol.com/stocks/marketstats/nsegainer/index.php")
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

   val allGainers =  page.div("tr")

   //val removeadj = allGainers.select("th.width").remove()

   /* import scala.collection.JavaConversions._
    for (element <- removeadj) {
      println("==== divsss ==========")
      println(element.toString)
    }
    println("AAAAAAAAAAAAAAAAAAAAAAAAAAAA"+ removeadj.first().toString)*/

      //foreach{ s  => if (s.mkString.contains("coronavirus")) println(s)}

    import scala.collection.mutable.ListBuffer

    var res = new ListBuffer[String]()


    import scala.collection.JavaConversions._

      import scala.collection.JavaConversions._
      for (row <- allGainers) {
        val tds = row.select("td")
        if (tds.size > 6) {
         /* System.out.println(tds.get(0).text + " 1=> " + tds.get(1).text +" 2=> " + tds.get(2).text + " 3=> " + tds.get(3).text +
          " 4=> " + tds.get(4).text +" 5=> " + tds.get(5).text + " 6=> " + tds.get(6).text +
            " 7=> " + tds.get(7).text +" 8=> " + tds.get(8).text + " 9=> " + tds.get(9).text)*/
          res += tds.get(0).text.toString
       //   System.out.println(tds.get(2).text + ":" + tds.get(3).text)

        }
      }

     val finalsList = res.toList
     val finalString = finalsList.mkString

     finalsList.foreach(println)

     // println("===BBBBBBBBBBBBBB===="+ finalsList.head)

    //page.hrefs.foreach{ println}




    val im= page.dom("img[src~=(?i)\\\\.(png|jpe?g|gif)]")


    val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss-SS'Z'");
    val  dateSelected = formatter.format(new Date());
    // java.time.LocalDateTime.now.toString

    val result = FileDTO(fileName = s"ScreenerCrawlResults${dateSelected}",fileType= "txt", content = finalString)
    //val result = FileDTO(fileName = s"ScreenerCrawlResults${dateSelected}",content = page.body.toString)
    page.addPageResultItem(result)


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

  override val pipelines: Set[Pipeline] = Set(FilePipeline(Some("C:\\Users\\91801\\Downloads\\chromedriver_win32")), ConsolePipeline)

  override val downloader: Downloader[_] = ChromeHeadlessDownloader

  override def cronExpression: Option[String] = Some("0 */2 * ? * *")


}

