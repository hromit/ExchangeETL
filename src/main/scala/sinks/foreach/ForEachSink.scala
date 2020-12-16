package sinks.foreach

import org.apache.spark.sql.{DataFrame, ForeachWriter}
import org.apache.spark.sql.streaming.{OutputMode, StreamingQuery, Trigger}
import sinks.StreamingSink
import org.apache.spark.sql._

class ForEachSink(trigger: Trigger = Trigger.Once(), outputmode: OutputMode = OutputMode.Update(), format: String, tableName: String, checkpointLocation: String, writer: ForeachWriter[Row]) extends StreamingSink{
  override def writeStream(data: DataFrame): StreamingQuery = {

    data.writeStream
        .queryName("ForeachQuery")
        .foreach(writer)
        .trigger(trigger)
        .outputMode(outputmode)
        .option("checkpointLocation","/tmp/foreach1")
        .start()
  }
}
