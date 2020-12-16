package streams

import crawlers.processor.PageProcessor
import utils.ConfigUtils

trait TestCrawlerTrait extends  PageProcessor{

  val enable: Boolean = ConfigUtils.getBooleanOpt(s"streams.${this.getClass.getSimpleName}.enable").getOrElse(false)

}
