package utils.logger

import org.slf4j.{Logger, LoggerFactory}

/**
 * @author : Romit Srivastava
 * @since: 8/4/20
 * @version: 1.0.0
 */

trait Logging {
protected val logger: Logger = LoggerFactory.getLogger(this.getClass)
}
