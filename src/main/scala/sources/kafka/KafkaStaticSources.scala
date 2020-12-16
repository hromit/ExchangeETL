package sources.kafka

import org.apache.spark.sql.{DataFrame, SparkSession}
import sources.StaticSources

class KafkaStaticSources(val spark: SparkSession) extends StaticSources{
    def readStatic() : DataFrame = ???
}
