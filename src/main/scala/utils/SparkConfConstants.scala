package utils

trait SparkConfConstants {

  final val SPARK_MASTER = "local[*]"
  final val CHECKPOINTLOCATION_KEY = "checkpointLocation"

  final val SPARK_SQL_SESSION_TIMEZONE_KEY = "spark.sql.session.timeZone"
  final val SPARK_SQL_SESSION_TIMEZONE_VALUE = "UTC"

  final val SPARK_SQL_SHUFFLE_PARTITIONS_KEY = "spark.sql.shuffle.partitions"
  final val SPARK_SQL_SHUFFLE_PARTITIONS_VALUE = "4"

  final val SPARK_SQL_CBO_ENABLED_KEY = "spark.sql.cbo.enabled"
  final val SPARK_SQL_CBO_ENABLED_VALUE = "true"

  final val SPARK_SERIALIZER_KEY = "spark.serializer"
  final val SPARK_SERIALIZER_VALUE = "org.apache.spark.serializer.KryoSerializer"

  final val SPARK_KRYOSERIALIZER_BUFFER_KEY = "SPARK_KRYOSERIALIZER_BUFFER_KEY"
  final val SPARK_KRYOSERIALIZER_BUFFER_VALUE = "24"

  final val SPARK_SQL_HIVE_HIVESERVER2_JDBC_URL_KEY = "spark.sql.hive.hiveserver2.jdbc.url"
  final val SPARK_SQL_HIVE_HIVESERVER2_JDBC_URL_VALUE = ""

  final val SPARK_DATASOURCE_HIVE_WAREHOUSE_METASTOREURI_KEY = ""
  final val SPARK_DATASOURCE_HIVE_WAREHOUSE_METASTOREURI_VALUE = ""

  final val SPARK_HADOOP_HIVE_ZOOKEEPER_QUORUM_KEY = ""
  final val SPARK_HADOOP_HIVE_ZOOKEEPER_QUORUM_VALUE = ""


  final val SPARK_DATASOURCE_HIVEWAREHOUSE_LOAD_STAGING_DIRECTORY_KEY = ""
  final val SPARK_DATASOURCE_HIVEWAREHOUSE_LOAD_STAGING_DIRECTORY_VALUE = ""


  final val SPARK_HADOOP_LLAP_DEAMON_SERVICE_HOSTS_KEY = ""
  final val SPARK_HADOOP_LLAP_DEAMON_SERVICE_HOSTS_VALUE = ""

  final val DATA_FORMAT_KAFKA = "org.apache.spark.sql.kafka010.KafkaSourceProvider"

  final val KAFKA_BOOTSTRAP_SERVERS_KEY = "kafka.bootstrap.servers"
  final val KAFKA_BOOTSTRAP_SERVERS_VALUE = "host:9093"

  final val SUBSCRIBE_KEY = "subscribe"
  final val SUBSCRIBE_VALUE = "testTopic"


  final val FAILONDATALOSS_KEY = "failOnDataLoss"
  final val FAILONDATALOSS_VALUE = "false"

  final val STARTINGOFFSETS_KEY = "startingOffsets"
  final val STARTINGOFFSETS_VALUE = ""


  final val KAFKASECURITYPROTOCOL_KEY = "kafka.security.protocol"
  final val KAFKASECURITYPROTOCOL_VALUE = "SASL_SSL"


  final val KAFKASSLKEYSTORELOCATION_KEY = "kafka.ssl.keystore.location"
  final val KAFKASSLKEYSTORELOCATION_VALUE = "local path .jks file"


  final val KAFKASSLKEYSTOREPASSWORD_KEY = "kafka.ssl.keystore.password"
  final val KAFKASSLKEYSTOREPASSWORD_VALUE = "password"


  final val KAFKASSLTRUSTSTORELOCATION_KEY = "kafka.ssl.truststore.location"
  final val KAFKASSLTRUSTSTORELOCATION_VALUE = "local path .jks file"

  final val KAFKASSLTRUSTSTOREPASSWORD_KEY = "kafka.ssl.truststore.password"
  final val KAFKASSLTRUSTSTOREPASSWORD_VALUE = "password"


  final val KAFKASSLKEYPASSWORD_KEY = "kafka.ssl.key.password"
  final val KAFKASSLKEYPASSWORD_VALUE = "password"


  final val KAFKASASLKERBEROSSERVICENAME_KEY = "kafka.sasl.kerberos.service.name"
  final val KAFKASASLKERBEROSSERVICENAME_VALUE = "kafka"


  final val KAFKASASLMECHANISM_KEY = "kafka.sasl.mechanism"
  final val KAFKASASLMECHANISM_VALUE = "GSSAPI"


  final val MAXOFFSETSPERTRIGGER_KEY = "maxOffsetsPerTrigger"
  final val MAXOFFSETSPERTRIGGER_VALUE = "60000"






}
