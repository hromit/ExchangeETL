package crawlers.downloader.proxy

import java.net.{HttpURLConnection, InetSocketAddress, URL}
import java.util.Objects
import java.util.concurrent.{ArrayBlockingQueue, ExecutorService, Executors}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}

import crawlers.downloader.proxy.ProxyStatusEnums.ProxyStatusEnums
import crawlers.downloader.proxy.ProxyProvider._
import crawlers.processor.PageProcessor
import crawlers.schedule.{ScheduleJobs, ScheduleJob}
import crawlers.spider.Spider

import scala.util.Try
import org.quartz.{Job, JobExecutionContext}
import org.slf4j.{Logger, LoggerFactory}
import utils.ReflectionUtils

object ProxyProvider {

  lazy val logger: Logger = LoggerFactory.getLogger(ProxyProvider.getClass)

  val checkThread: ExecutorService  = Executors.newFixedThreadPool(5)

  val proxyList: ArrayBlockingQueue[ProxyDTO] = new ArrayBlockingQueue[ProxyDTO](100)

  val proxySpiderCrawling: AtomicBoolean = new AtomicBoolean(false)

  private lazy val proxyCrawlerList: Seq[Spider] = ReflectionUtils
    .implementationClasses(
      classOf[PageProcessor],
      "crawler.downloader.proxy.crawler"
    )
    .map(proxy => Spider(pageProcessor = proxy.newInstance()))

  private def crawlCronJob(restart: Boolean = false) =
    if (restart) proxyCrawlerList.foreach(_.restart())
    else proxyCrawlerList.foreach(_.start())

  def startProxyCrawl(restart: Boolean = false): Unit =
    if (!proxySpiderCrawling.getAndSet(true)) {
      crawlCronJob(restart)
      ScheduleJobs.addJob(scheduleJob = ScheduleJob(jobName = "proxy-check", cronExpression = "*/2 * * ? * *", task = classOf[ProxyCheckScheduleJob]))
    }

  def getProxy: Option[ProxyDTO] = Option(proxyList.peek()).filter(_.usability > 0.5)
}


class ProxyCheckScheduleJob extends Job {

  override def execute(context: JobExecutionContext): Unit = {
    def checkProxy() = Option(proxyList.poll()).foreach{ proxy =>
      proxy.usabilityCheck(proxy.connect2Baidu()) match {
        case (_, true) =>
          proxyList.put(proxy)
        case (_, _) =>
      }
    }

    if(logger.isDebugEnabled){
      logger.debug(s"proxy list is ${proxyList.size()}")
    }

    (1 to 5).foreach{ _ =>
      checkThread.execute(new Runnable {
        override def run(): Unit = checkProxy()
      })
    }
  }

}

final case class ProxyDTO(
     host: String,
     port: Int,
     username: Option[String] = None,
     password: Option[String] = None,
     var status: ProxyStatusEnums = ProxyStatusEnums.IDLE,
     var usability: Float = 0f) {

  val checkUrl: URL                  = new URL("http://baidu.com")
  val checkTimes: AtomicInteger      = new AtomicInteger(0)
  val successTimes: AtomicInteger    = new AtomicInteger(0)
  val usabilityLimit                 = 0.5

  def connect2Baidu(): Boolean = {
    import java.net.Proxy
    Try {
      val proxy       = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port))
      val connection  = checkUrl.openConnection(proxy).asInstanceOf[HttpURLConnection]
      connection.setConnectTimeout(1000)
      connection.setReadTimeout(1000)

      connection.getResponseCode == 200

    }.recover {
      case _: Throwable =>
        false
    }.get
  }

  def usabilityCheck(checkMay: Boolean): (Float, Boolean) = {
    usability = {
      val checkTimeValue = checkTimes.incrementAndGet()
      if (checkMay)
        successTimes.incrementAndGet() / checkTimeValue
      else
        successTimes.get() / checkTimeValue
    }
    (usability, usability > usabilityLimit)
  }

  override def hashCode(): Int = Objects.hash(this.host.asInstanceOf[Object], this.port
    .asInstanceOf[Object])

  override def equals(obj: Any): Boolean = obj match {
    case t: ProxyDTO =>
      t.host == this.host && t.port == this.port
  }

  override def toString: String = s"${host}:${port}"
}

object ProxyStatusEnums extends Enumeration {
  type ProxyStatusEnums = Value
  val USING: Value = Value("using")
  val IDLE: Value = Value("idle")
}