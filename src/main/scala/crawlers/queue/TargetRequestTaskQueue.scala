package crawlers.queue


import java.util.concurrent.LinkedBlockingDeque

import crawlers.dto.RequestSetting
import crawlers.queue.duplicate.{DuplicateRemovedStrategy, HashMapStrategy}

class TargetRequestTaskQueue(duplicateRemovedStrategy: DuplicateRemovedStrategy = HashMapStrategy) extends DuplicateRemovedQueue(duplicateRemovedStrategy) {
  private lazy val queue: LinkedBlockingDeque[RequestSetting] = new LinkedBlockingDeque[RequestSetting]()

  override protected def pushWhenNoDuplicate(request: RequestSetting): Unit = this.queue.add(request)

  override protected def doPoll(): Option[RequestSetting] = Option(this.queue.poll())

  override def isEmpty: Boolean = queue.isEmpty
}
