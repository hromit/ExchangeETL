package utils

import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf

import java.io.File

trait SparkConfigurations extends SparkConfConstants{

  val extraConfigurations : Traversable[(String,String)]

  private lazy val conf = new SparkConf()
                               .set(SPARK_SQL_SESSION_TIMEZONE_KEY,SPARK_SQL_SESSION_TIMEZONE_VALUE)
                               .set(SPARK_SQL_SHUFFLE_PARTITIONS_KEY,SPARK_SQL_SHUFFLE_PARTITIONS_VALUE)
                               .set(SPARK_SQL_CBO_ENABLED_KEY,SPARK_SQL_CBO_ENABLED_VALUE)
                               .set(SPARK_SERIALIZER_KEY,SPARK_SERIALIZER_VALUE)
                               .set(SPARK_KRYOSERIALIZER_BUFFER_KEY,SPARK_KRYOSERIALIZER_BUFFER_VALUE)
                               .set(SPARK_SQL_HIVE_HIVESERVER2_JDBC_URL_KEY,SPARK_SQL_HIVE_HIVESERVER2_JDBC_URL_VALUE)
                               .set(SPARK_DATASOURCE_HIVE_WAREHOUSE_METASTOREURI_KEY,SPARK_DATASOURCE_HIVE_WAREHOUSE_METASTOREURI_VALUE)
                               .set(SPARK_HADOOP_HIVE_ZOOKEEPER_QUORUM_KEY,SPARK_HADOOP_HIVE_ZOOKEEPER_QUORUM_VALUE)
                               .set(SPARK_DATASOURCE_HIVEWAREHOUSE_LOAD_STAGING_DIRECTORY_KEY,SPARK_DATASOURCE_HIVEWAREHOUSE_LOAD_STAGING_DIRECTORY_VALUE)
                               .set(SPARK_HADOOP_LLAP_DEAMON_SERVICE_HOSTS_KEY,SPARK_HADOOP_LLAP_DEAMON_SERVICE_HOSTS_VALUE)
                               .setAll(extraConfigurations)

  implicit lazy val spark: SparkSession = SparkSession.builder()
                                                      .master("local")
                                                      .config(conf)
                                                      .getOrCreate()


}
