package crawlers.pipeline

import java.util.concurrent.atomic.AtomicBoolean

import utils.logger.Logging

trait Pipeline extends Logging{

  private val inited = new AtomicBoolean(false)

  def open(): Unit =
    if (inited.compareAndSet(false, true)) {
      init()
    }

  protected def init(): Unit = ()

  def process[Result](pageResultItem: (String, Result)): Unit
}
