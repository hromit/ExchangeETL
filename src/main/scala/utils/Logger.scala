package utils

import org.apache.log4j
import org.apache.log4j.{Level, LogManager}

trait Logger {

  val log: log4j.Logger =  LogManager.getRootLogger
  log.setLevel(Level.DEBUG)

  def log(msg: String): Unit = {
    println(msg)
  }

}
