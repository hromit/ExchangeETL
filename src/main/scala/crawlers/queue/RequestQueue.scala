package crawlers.queue

import java.util.concurrent.TimeUnit

import crawlers.dto.RequestSetting
import org.slf4j.{Logger, LoggerFactory}

trait RequestQueue {
  protected val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def push(request: RequestSetting): Unit

  def poll(): Option[RequestSetting] =
    doPoll().map { r =>
      TimeUnit.MILLISECONDS.sleep(r.sleepTime.toMillis)
      r
    }

  protected def doPoll(): Option[RequestSetting]

  def isEmpty: Boolean

  def nonEmpty: Boolean = !isEmpty


}
