package crawlers.queue
import crawlers.dto.RequestSetting
import crawlers.queue.duplicate.DuplicateRemovedStrategy

abstract class DuplicateRemovedQueue(duplicateRemovedStrategy: DuplicateRemovedStrategy) extends RequestQueue {

  override def push(request: RequestSetting): Unit =
    if (isNotDuplicateRequest(request)) {
      pushWhenNoDuplicate(request)
    }

  private def isNotDuplicateRequest(requestHeaderGeneral: RequestSetting): Boolean =
    requestHeaderGeneral.method match {
      case "GET" =>
        !duplicateRemovedStrategy.isDuplicate(requestHeaderGeneral.url.get)
      case "POST" =>
        !duplicateRemovedStrategy.isDuplicate(
          requestHeaderGeneral.url.get + requestHeaderGeneral.requestBody
              .getOrElse("")
        )
      case other =>
        logger.warn(s"unknown request method type: ${other}")
        true
    }


  protected def pushWhenNoDuplicate(request: RequestSetting): Unit

}
