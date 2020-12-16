package sources.hive

import org.apache.spark.sql.{DataFrame, SparkSession}
import sources.StaticSources

class HiveReferenceTables(val spark: SparkSession) extends StaticSources{

  def readTestBatchFromHive: DataFrame = ???

}
