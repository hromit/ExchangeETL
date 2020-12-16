package crawlers.queue.duplicate

import java.util.concurrent.ConcurrentHashMap

import scala.concurrent.duration._

object HashMapStrategy extends DuplicateRemovedStrategy {
  private[this] val urlMap: ConcurrentHashMap[Int, Long] = new ConcurrentHashMap()

  override def isDuplicate(url: String): Boolean = {
    val urlHashCode = url.hashCode

    urlMap.containsKey(urlHashCode) match {
      case duplicated @ true if (passedMinutes(urlMap.get(urlHashCode), 10 minutes)) =>
        urlMap.put(urlHashCode, System.currentTimeMillis())
        !duplicated
      case duplicated @ true =>
        duplicated
      case nonDuplicated @ false =>
        urlMap.put(urlHashCode, System.currentTimeMillis())
        nonDuplicated
    }

  }

  private[this] def passedMinutes(latest: Long, duration: Duration) = (latest - System.currentTimeMillis()) > duration.toMillis
}
