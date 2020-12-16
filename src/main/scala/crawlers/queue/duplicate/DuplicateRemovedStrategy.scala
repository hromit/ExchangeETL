package crawlers.queue.duplicate

trait DuplicateRemovedStrategy {

  def isDuplicate(url: String): Boolean

}
