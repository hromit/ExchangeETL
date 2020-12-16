package crawlers.spider

import java.util.concurrent.ConcurrentHashMap

import crawlers.downloader.proxy.ProxyProvider
import crawlers.schedule.{ScheduleJobs, ScheduleJob}

import org.quartz.{Job, JobExecutionContext}

object SpiderPool {
  private[this] val spiders = new ConcurrentHashMap[String, Spider]()

  def addSpider(spider: Spider): Unit = {
   spiders.putIfAbsent(spider.name, spider)

   spider.pageProcessor.cronExpression.foreach {cron =>
     ScheduleJobs.addJob(ScheduleJob(jobName = spider.name, cronExpression = cron, task = classOf[SpiderScheduleJob]))
   }

   if (spider.pageProcessor.requestSetting.useProxy) {
     ProxyProvider.startProxyCrawl()
   }
  }

  def removeSpider(spider: Spider): Spider = spiders.remove(spider.name)

  def getSpiderByName(name: String): Option[Spider] = Option(spiders.get(name))

  def fetchAllSpiders(): Array[Spider] = spiders.values().toArray().map(_.asInstanceOf[Spider])

  def fetchAllUsingProxySpiders(): Array[Spider] = fetchAllSpiders().filter(_.pageProcessor.requestSetting.useProxy)

}
class SpiderScheduleJob() extends Job {
  override def execute(jobExecutionContext: JobExecutionContext): Unit =
    SpiderPool.getSpiderByName(jobExecutionContext.getJobDetail.getKey.getName)
    .foreach(_.restart())
}
