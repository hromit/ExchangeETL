package sinks.console

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.{OutputMode, StreamingQuery, Trigger}
import sinks.StreamingSink

class ConsoleSink(trigger: Trigger = Trigger.Once(), outputMode: OutputMode = OutputMode.Update()) extends StreamingSink{

  override def writeStream(data: DataFrame): StreamingQuery = {

    data.writeStream
        .format("console")
        .trigger(trigger)
        .outputMode(outputMode)
        .option("checkpointLocation","/tmp/console1")
        .start()
  }
}
