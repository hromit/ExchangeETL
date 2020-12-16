package sources.kafka

import sources.StreamingSources
import org.apache.spark.sql.{DataFrame, SparkSession}
import utils.SparkConfConstants
import za.co.absa.abris.avro.read.confluent.SchemaManager
import za.co.absa.abris.avro.schemas.policy.SchemaRetentionPolicies.RETAIN_SELECTED_COLUMN_ONLY
import za.co.absa.abris.avro.AvroSerDe._

class KafkaStreamingSources(val spark: SparkSession) extends StreamingSources with SparkConfConstants{


  override def readTestStream(): DataFrame = {
    loadStream("testKafkaStream","earliest","latest")
  }

  def loadStream(topicName: String, startingOffsets: String, schemaId: String) = {

    val schemaRegistryConf = Map(
      SchemaManager.PARAM_SCHEMA_REGISTRY_URL                 -> ":8081",
      SchemaManager.PARAM_SCHEMA_REGISTRY_TOPIC               -> topicName,
      SchemaManager.PARAM_VALUE_SCHEMA_NAMING_STRATEGY        -> SchemaManager.SchemaStorageNamingStrategies.TOPIC_NAME,
      SchemaManager.PARAM_VALUE_SCHEMA_ID                     -> schemaId
    )

    val kafkaConf = Map(
      SUBSCRIBE_KEY -> topicName,
      FAILONDATALOSS_KEY -> FAILONDATALOSS_VALUE,
      STARTINGOFFSETS_KEY -> STARTINGOFFSETS_VALUE,
      KAFKASECURITYPROTOCOL_KEY -> KAFKASECURITYPROTOCOL_VALUE,
      KAFKASSLKEYSTORELOCATION_KEY -> KAFKASSLKEYSTORELOCATION_VALUE,
      KAFKASSLKEYSTOREPASSWORD_KEY -> KAFKASSLKEYSTOREPASSWORD_VALUE,
      KAFKASSLTRUSTSTORELOCATION_KEY -> KAFKASSLTRUSTSTORELOCATION_VALUE,
      KAFKASSLTRUSTSTOREPASSWORD_KEY -> KAFKASSLTRUSTSTOREPASSWORD_VALUE,
      KAFKASSLKEYPASSWORD_KEY -> KAFKASSLKEYPASSWORD_VALUE,
      KAFKASASLKERBEROSSERVICENAME_KEY -> KAFKASASLKERBEROSSERVICENAME_VALUE,
      KAFKASASLMECHANISM_KEY -> KAFKASASLMECHANISM_VALUE,
      MAXOFFSETSPERTRIGGER_KEY -> MAXOFFSETSPERTRIGGER_VALUE
     )

    spark.readStream
     .format(DATA_FORMAT_KAFKA)
     .options(kafkaConf)
     .option("kafka.bootstrap.servers",":9093")
     .load()
     .fromConfluentAvro("value",None,Some(schemaRegistryConf)) (RETAIN_SELECTED_COLUMN_ONLY)
  }

}
