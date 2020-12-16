package crawlers.queue.duplicate

import scala.collection.BitSet

object BitSetStrategy extends DuplicateRemovedStrategy {
  var urlBitSet = BitSet.empty

  override def isDuplicate(url: String): Boolean = {
    val urlHashCode = urlToHashCode(url)
    val isDuplicate = urlBitSet.contains(urlHashCode)

    if (!isDuplicate) {
      urlBitSet += urlHashCode
    }

    isDuplicate
  }

  def urlToHashCode(url: String) : Int =
    url.hashCode & 0x7FFFFFFF
}
