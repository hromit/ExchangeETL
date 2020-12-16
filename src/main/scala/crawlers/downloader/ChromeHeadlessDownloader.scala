package crawlers.downloader

import java.io.File
import java.util
import java.util.logging.Level

import utils.{ConfigUtils, JsonUtils}
import crawlers.dto.{Page, RequestSetting, XhrResponse}
import crawlers.downloader.ChromeHeadlessConfig._
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.logging.{LogType, LoggingPreferences}
import org.openqa.selenium.remote
import org.openqa.selenium.remote.UnreachableBrowserException

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.util.control.NonFatal


object ChromeHeadlessDownloader extends Downloader[ChromeDriver] {
  override protected def doDownload(requestSetting: RequestSetting): Future[Page] = {

    println("ChromeHeadlessDownloader DO DOWNLOAD METHOD")
    val client = getOrCreateClient(requestSetting)

    Future {
      try {
        val driver = client.driver

        driver.get(requestSetting.url.get)
        val performanceLog = driver.manage().logs().get(LogType.PERFORMANCE)

        val iterator             = performanceLog.iterator()
        var returnAllXhrRequest  = true
        val xhrResponseBuffer    = new ListBuffer[XhrResponse]
        while (iterator.hasNext && returnAllXhrRequest) {
       //   println("===== INSIDE ChromeHeadless DO DOWNLOAD WHILE")
          val xhrResponse = iterator.next()
         // println("=== xhrresponse ====="+ xhrResponse)
          val message     = JsonUtils.parseFrom[Map[String, Any]](xhrResponse.getMessage)
            .get("message").get.asInstanceOf[Map[String, Any]]


          message.get("params").foreach {
            case params: Map[String, Any] @ unchecked =>
              val headers = params.getOrElse("headers", Map.empty[String, Any]).asInstanceOf[Map[String, Any]]
              getXhrRequestUriByHeaders(headers).foreach{
                case xhrResponseUri: String if requestSetting.xhrRequests.contains(xhrResponseUri) =>
                 // println("=== INSIDE FOR EACH ======="+ xhrResponseUri)
                  xhrResponseBuffer.append(XhrResponse(xhrResponseUri, getXhrResponse(driver, params.get("requestId").get.asInstanceOf[String])))
                case _ if (xhrResponseBuffer.size == requestSetting.xhrRequests.size) =>
                 // println("==== printing false inside looop=========")
                  returnAllXhrRequest = false
                case _ =>

              }
          }
        }

       // println("======= INSIDE CHROMEHEADLESS PRINTING XHRRESPONSEBUFFER" + returnAllXhrRequest)
       // println("======= INSIDE CHROMEHEADLESS PRINTING XHRRESPONSEBUFFER" + xhrResponseBuffer.size)
       // println("============== Creating Page from ChromeHeadless do download ===========================")
       // println("======= INSIDE ")
        /*
             isDownloadSuccess: Boolean = true,
             bytes: Option[Array[Byte]] = None,
             responseHeaders: Map[String, String] = Map.empty[String, String],
             xhrResponses: Seq[XhrResponse] = Seq.empty[XhrResponse],
             requestSetting: RequestSetting
         */

        Page(requestSetting = requestSetting, bytes = Some(driver.getPageSource.getBytes()),
        xhrResponses = xhrResponseBuffer.toSeq)
      } catch {
        case NonFatal(exception) =>
          Page.failed(requestSetting, exception)
      } finally {
        client.decrement()
      }
    }(crawlers.actor.ExecutionContexts.downloadDispatcher)
  }




  private def getXhrResponse(driver: ChromeDriver, requestId: String): Map[String, Any] = {
    println("==== inside ChromeHeadlessDownloader getXhrResponse ")
    val cdpMap = new util.HashMap[String, Object]()
    cdpMap.put("requestId", requestId)
    driver.executeCdpCommand("Network.getResponseBody", cdpMap).asScala.toMap
  }

  private def getXhrRequestUriByHeaders(headers: Map[String, Any]): Option[String] = {
    //println("=== prinitng converted headers ==="+ headers.head)
   // println("=== prinitng converted headers ==="+ headers.size)
    headers.get("x-requested-with") match {
      case Some("XMLHttpRequest") =>
        val schema: String = headers.getOrElse(":scheme","").asInstanceOf[String]
        val domain: String = headers.getOrElse(":authority","").asInstanceOf[String]
        val uri   : String = headers.get(":path").fold[String]("") {
          case p: String =>
            val queryIndex = p.indexOf('?')
            p.substring(0, if (queryIndex > 0) queryIndex else p.length)
        }
      //  println(s"$schema://$domain$uri")
        Some(s"$schema://$domain$uri")
      case _ =>
       // println("=== None ============")
        None
    }
  }

  private[this] def buildOptions(requestSetting: RequestSetting): ChromeOptions = {
    val options = new ChromeOptions
    options.setExperimentalOption("excludeSwitches", Array[String]("enable-automation"))


    val perf = new util.HashMap[String, Any]()
    perf.put("enableNetwork",true)
    options.setExperimentalOption("prefs", perf)


    val logPrefs = new LoggingPreferences
    logPrefs.enable(LogType.PERFORMANCE, Level.ALL)
    logPrefs.enable(LogType.SERVER, Level.ALL)
    logPrefs.enable(LogType.BROWSER, Level.ALL)
    logPrefs.enable(LogType.DRIVER, Level.ALL)
    logPrefs.enable(LogType.CLIENT, Level.ALL)
    options.setCapability("goog:loggingPrefs", logPrefs)
    options.addArguments(
      "--no-sandbox",
      "--headless",
      "--start-maximized",
      "--disable-dev-shm-usage",
      "--disable-plugins-discovery",
      "--enable-logging",
      "--v=1",
      "--disable-gpu",
      "--ignore-certificate-errors",
      s"--user-agent=${requestSetting.userAgent}"
    )
    options
  }

  override protected def getOrCreateClient(requestSetting: RequestSetting): DownloaderClient[ChromeDriver] =
    getDownloaderClient(requestSetting.domain) {
      System.setProperty("webdriver.chrome.driver", chromeDriverPath)
      System.setProperty("webdriver.chrome.logfile", chromeDriverLog)

      val driver = new ChromeDriver(buildOptions(requestSetting))
      val map    = new util.HashMap[String, Object]()

      map.put(
        "source",
        """
          |Object.defineProperty(navigator, 'webdriver', {
          |      get: () => false
          |});
          |Object.defineProperty(navigator, 'plugins', {
          |      get: () => [1, 2, 3, 4, 5]
          |});
          |Object.defineProperty(navigator, 'languages', {
          |      get: () => ["zh-CN","zh","en-US","en"]
          |});
          |""".stripMargin
      )
      driver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", map)

      driver

    }

  override protected def closeClient(): Unit = closeDownloaderClient { driver =>
    try(driver.quit())
    catch {
      case _: UnreachableBrowserException =>
        ()
      case e: Throwable =>
        throw e
    }
  }
}






object ChromeHeadlessConfig {
  lazy val chromeDriverPath: String = ConfigUtils.getStringOpt("crawlers.chrome.driver")
   .getOrElse("C:\\Users\\91801\\Downloads\\chromedriver_win32\\chromedriver.exe")
  lazy val chromeDriverLog: String  = ConfigUtils.getStringOpt("crawlers.chrome.log")
   .getOrElse("C:\\Users\\91801\\Downloads\\chromedriver_win32\\chromedriver.log")

  def chromeDriverNotExecutable: Boolean = {
    val file       = new File(chromeDriverPath)
    val canExecute = file.exists() && file.canExecute
    !canExecute
  }
}
