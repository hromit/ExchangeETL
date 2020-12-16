package crawlers.selector

trait Selector {
  def select(text: String): String
}
