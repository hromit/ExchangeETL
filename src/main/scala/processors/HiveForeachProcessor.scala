package processors

import org.apache.spark.sql.{ForeachWriter, Row}
import utils.SparkConfigurations

class HiveForeachProcessor(appName: String) extends ForeachWriter[Row] with SparkConfigurations{
  override def open(partitionId: Long, version: Long): Boolean = {
    println("== Open Connection =")
    true
  }

  override def process(value: Row): Unit = {

  }

  override def close(errorOrNull: Throwable): Unit = {
    println("== Close Connection ==")
  }

  override val extraConfigurations: Traversable[(String, String)] = Map(
    "spark.app.name" -> appName,
    "spark.sql.shuffle.partitions" -> "1"
  )
}
