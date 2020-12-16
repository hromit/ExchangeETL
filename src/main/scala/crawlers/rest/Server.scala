package crawlers.rest

import utils.logger.Logging
import utils.ConfigUtils

import java.util.concurrent.Executors

import scala.concurrent.{ ExecutionContext, Future}
import scala.util.control.NonFatal

import crawlers.rest.Router


/**
 * @author: Romit srivastava
 * @since: 11/4/20 14:27
 * @version: 1.0.0
 */

trait Server extends Logging{

  @volatile var running = false

  def Start(routes: Set[Router]): Boolean = {
    if(!running) {
      Future {
        running = true
        try {
          doStart(routes)
        } catch {
          case NonFatal(e) =>
            logger.error(e.getLocalizedMessage)
            running = false
        }
      }(ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor()))
    } else {
      running = true
    }
    running

  }

  protected  def doStart(routes: Set[Router]): Unit

  val defaultRoutes: Set[Router] = Set(SpiderStatusRoute)

  val port: Int = ConfigUtils.getIntOpt("crawler.server.port").getOrElse(19000)

}


object Server{
  val serverInstance = NettyServer

 // def start(routes: Set[Router]) = Set.empty([Router]): Boolean = serverInstance.st

  def running: Boolean = serverInstance.running
}
