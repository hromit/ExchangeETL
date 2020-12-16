package sinks

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.StreamingQuery

trait StreamingSink {

  def writeStream(data: DataFrame) : StreamingQuery

}
