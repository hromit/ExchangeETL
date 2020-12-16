package streams

import org.apache.spark.sql.SparkSession
import processors.TestProcessor
import sinks.StreamingSink
import sinks.console.ConsoleSink
import sources.hive.HiveReferenceTables
import sources.kafka.{KafkaStaticSources, KafkaStreamingSources}
import org.apache.spark.sql.streaming.{OutputMode,Trigger}

object TestStream extends App{

  val processor = new TestProcessor("TestProcessor") {
    override def createStreamingSources(spark: SparkSession): KafkaStreamingSources = new KafkaStreamingSources(spark)

    override def createStaticSources(spark: SparkSession): KafkaStaticSources = new KafkaStaticSources(spark)

    override def getReferenceTables(spark: SparkSession): HiveReferenceTables = new HiveReferenceTables(spark)

    override def createStreamingSink: StreamingSink = {
      import scala.concurrent.duration._
      new ConsoleSink(trigger = Trigger.ProcessingTime(20.seconds), outputMode = OutputMode.Append())
    }
  }

  processor.start()

}
