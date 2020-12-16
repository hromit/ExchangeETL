package crawlers.rest

import io.netty.handler.codec.http.FullHttpRequest
import utils.JsonUtils

/**
 * @author: Romit Srivastava
 * @since: 11/4/20 14:47
 * @version: 1.0.0
 *
 */

trait Router {

  def method: String

  def route: String

  def handleRequest(request: FullHttpRequest): Array[Byte]

  implicit def toBytes(content: String): Array[Byte] = content.getBytes()

}

object SpiderStatusRoute extends Router {
  override def method: String = "GET"

  override def route: String = "/spiders"

  override def handleRequest(request: FullHttpRequest): Array[Byte] = {
    val results = "" // todoooooooooo
    JsonUtils.toJson(results)
  }

}
