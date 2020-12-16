package utils

import org.apache.spark.sql.SparkSession

object TestS3connection extends SparkConfigurations {

  val extraConfigurations: Traversable[(String, String)] = Map("spark.app.name" -> "TestS3Connection",
    "fs.s3a.impl"-> "org.apache.hadoop.fs.s3a.S3AFileSystem",
    "fs.s3a.access.key"-> "AKIAIRVVVKED5NG2ZROA",
    "fs.s3a.secret.key" -> "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",
    "fs.s3a.connection.maximum" -> "500",
    "spark.serializer" -> "org.apache.spark.serializer.KryoSerializer")

  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir","C:\\winutils")

    val readCSV = spark.read.csv("s3a://datadondaily/2018/NSE/63MOONS.csv")
    readCSV.show(20)
  }






 /* val spark = SparkSession
               .builder
               .appName("TestS3Connection")
               .master("local[*]")
               .config("fs.s3a.impl","org.apache.hadoop.fs.s3a.S3AFileSystem")
               .config("fs.s3a.access.key","AKIAIRVVVKED5NG2ZROA")
               .config("fs.s3a.secret.key","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")
               .config("fs.s3a.connection.maximum","500")
               .config("spark.serializer","org.apache.spark.serializer.KryoSerializer")
               .getOrCreate()

  val readCSV = spark.read.csv("s3a://datadondaily/2018/NSE/63MOONS.csv")

  readCSV.show(20)*/



}
