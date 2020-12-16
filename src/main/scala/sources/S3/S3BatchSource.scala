package sources.S3

import org.apache.spark.sql.{DataFrame, SparkSession}
import sources.StaticSources

class S3BatchSource(val spark: SparkSession) extends StaticSources{
   def readStatic(): DataFrame = ???
}
