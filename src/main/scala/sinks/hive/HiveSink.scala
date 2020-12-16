package sinks.hive

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.{OutputMode, StreamingQuery, Trigger}
import sinks.StreamingSink

class HiveSink(trigger: Trigger = Trigger.Once(), outputMode: OutputMode = OutputMode.Update(), format: String, tableName: String, checkpointLocation: String) extends StreamingSink{
  override def writeStream(data: DataFrame): StreamingQuery = {

    data.writeStream
        .format("HiveWareHouseSession.STREAM_TO_STREAM")
        .option("database","testdb")
        .trigger(trigger)
        .outputMode(outputMode)
        .option("metastoreUri","thrift://ssss:9093")
        .option("checkpointLocation",checkpointLocation)
        .start()

  }
}
