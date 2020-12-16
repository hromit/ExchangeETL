package streams

import streams.SparkNLPTestMain.spark
import utils.SparkConfigurations
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType, StructField, StructType, StringType}

import org.apache.spark.sql.types._
import org.apache.spark.sql._

import scala.collection.mutable.ListBuffer

object SparkTestTransform extends  SparkConfigurations{
  override val extraConfigurations: Traversable[(String, String)] = Map("spark.app.name" -> "TestS3Connection",
    "spark.serializer" -> "org.apache.spark.serializer.KryoSerializer")

  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir","C:\\winutils")
    val df = spark.read.text("C:\\Users\\91801\\Downloads\\chromedriver_win32\\MoneyControlFrontPoliticsHeadlines.txt")
    df.printSchema()
    val testNews = df.first().toString().split("June ")
    // testNews.foreach(println)

  // var testBuffer: ListBuffer[(String, String)] = ListBuffer()
    var a : List[(String,String)] = List()


    for (news <- testNews){
      a = a:+((news.toString.slice(0,2),news.trim))
    }

    a.tail

    val schema = StructType(
      List(
        StructField("DATEeee", StringType, true),
        StructField("NEWS", StringType, true)

      )
    )


    import spark.implicits._

   // val s = spark.sparkContext.parallelize(testNews.toList)
    val rdd = spark.sparkContext.parallelize(a.tail).map (x => Row(x._1,x._2.split("IST")(1)))
    spark.createDataFrame(rdd,schema).withColumn("date", current_date()).show(20, false)

    // s.toDF.show(20, false)



    //testBuffer.toList.foreach(println)
   // println(splitIntoList(1))



    //println(df.count())
   // println(df.show(false))


    /**
      LETS BUILD THIS MORE --------------------------------------

      MOENYCONTROL COMPLETE FLOW ====>
      LETS GO THROUGH ITs WEBSITE & COLLECT THE INFO FROM ALL THE REQUIRED PAGES,
      THEN PROCESS THOSE PAGES , CONVERT IT INTO DATAFRAMES & STORE IT IN S3.

      START CODING IN SCALA  ----- LEARN ABOUT STOCKS & ALL MONEYCONTROL PAGES

      WHAT OTHER DIFFERENT SOURCES OF NEWS CAN BE  ---?
      HOW ABOUT INCREASING FREQUENCY TO THIS FEEDS
      HOW ABOUT CONTINUOUS FEEDS & NEWS
      THEN INSTEAD OF IT BE A PULL BASED FEEDS IT WILL BE PUSH BEASED FEEDS
      HOW ABOUT GETTING STOCKS DATA 





     */




    /*
      This work was done to parse top gainers  directly using Dataframe
    val df = spark.read.text("C:\\Users\\91801\\Downloads\\chromedriver_win32\\MoneyControlTopGainers2020-06-13T17-57-20-893Z.html")

     // df.show(100, false)

      println("======= FILTERED ===============================================================")
     df.filter((trim(col("value")) =!="<div>"))
       .filter((trim(col("value")) =!="</div>"))
       .show(1000, false)
     // println(df.filter(trim(col("value"))==="<div>" or trim(col("value"))==="</div>").count())

      //df.filter(trim(col("value")).notEqual("<div>") or trim(col("value")).notEqual("</div>") ).show(100, false)*/

  }

}
