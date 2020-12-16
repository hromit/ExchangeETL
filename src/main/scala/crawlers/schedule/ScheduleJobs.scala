package crawlers.schedule

/**
 * @author: Romit Srivastava
 * @since: 11/4/20 13:59
 * @version: 1.0.0
 */

import org.quartz.impl.StdSchedulerFactory
import org.quartz.{ JobBuilder, _}

object ScheduleJobs {

  private lazy val scheduler = new StdSchedulerFactory().getScheduler()

  def addJob[C <: Job](scheduleJob: ScheduleJob[C]): Unit =
    if(!scheduler.checkExists(scheduleJob.jobKey)) {
      val trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(scheduleJob.cronExpression)).build()
      val job     = JobBuilder.newJob(scheduleJob.task).withIdentity(scheduleJob.jobKey).build

      scheduler.scheduleJob(job, trigger)
      scheduler.startDelayed(1)
    }
}


case class ScheduleJob[C <: Job](jobName: String, cronExpression: String, task: Class[C], groupName: Option[String] = None) {
  val group: String = groupName.getOrElse(jobName)
  val jobKey        = new JobKey(jobName, group)
}
