package sources.S3

import org.apache.spark.sql.{DataFrame, SparkSession}
import sources.StreamingSources
import utils.SparkConfConstants

class S3StreamingSource(val spark: SparkSession) extends StreamingSources with SparkConfConstants{
  override def readTestStream(): DataFrame = ???
}
