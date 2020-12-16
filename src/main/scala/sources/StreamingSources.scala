package sources

import org.apache.spark.sql.DataFrame

trait StreamingSources {

  // Common functions for all sources
  def readTestStream(): DataFrame

}
