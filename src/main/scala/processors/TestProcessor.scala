package processors

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.streaming.StreamingQuery
import utils.{Logger, SparkConfigurations}
import org.apache.spark.sql.{DataFrame, SparkSession}
import sinks.StreamingSink
import sources.hive.HiveReferenceTables
import sources.kafka.{KafkaStaticSources, KafkaStreamingSources}
import org.apache.spark.sql.functions.{array, col, concat, lit, row_number, substring, trim, when}

abstract class TestProcessor(appName: String) extends SparkConfigurations with Logger {

   val extraConfigurations: Traversable[(String, String)] =  Map("spark.app.name" -> appName,
    "spark.sql.shuffle.partitions" -> "1")

    def start(): Unit = {
      val streamingSources = createStreamingSources(spark)
      val staticSources = createStaticSources(spark)

      val testStream = streamingSources.readTestStream()
      testStream.printSchema()

      val result = testStream

      val query = startStreamingSink(result,createStreamingSink)
      query.awaitTermination()

    }

    def createStreamingSources(spark: SparkSession):  KafkaStreamingSources
    def createStaticSources(spark: SparkSession): KafkaStaticSources
    def getReferenceTables(spark: SparkSession): HiveReferenceTables

     def createStreamingSink: StreamingSink

    private def startStreamingSink[T <: StreamingSink] (data: DataFrame, sink: T): StreamingQuery = {
      sink.writeStream(data)
    }

    def getLatestRecs(df:DataFrame, partition_col: List[String], sortCols: List[String]) : DataFrame = {
      val part = Window.partitionBy(partition_col.head,partition_col:_*).orderBy(array(sortCols.head, sortCols:_*).desc)
      val rowDF = df.withColumn("rn",row_number().over(part))
      val res = rowDF.filter("rn==1").drop("rn")
      res
    }

}
