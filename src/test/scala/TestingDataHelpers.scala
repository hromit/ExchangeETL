import org.apache.spark.sql.{Column, DataFrame}
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.apache.spark.sql.functions.{col, concat, current_timestamp, length, lit, month, trim, when, year}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._


trait TestingDataHelpers extends TestingSuite {

  private val testSchema = StructType(Array(
    StructField("testId",LongType),
    StructField("testName",StringType)))

  def getDataATM(): List[String] = {
    List("1,Romit","2,Garvit")
  }

  def convertTestDataToDf(inputData: List[String]): DataFrame = {
    val rdd = sparkTest.sparkContext.parallelize(inputData) // RDD[STRING]
    val rddSplitted = rdd.map(_.split(",")) //RDD[Array[String]]
    val rddRows: RDD[Row] = rddSplitted.map(arr => Row(arr(0).toLong,arr(1))) //rowRDD

    val df = sparkTest.createDataFrame(rddRows,testSchema)
    df
  }
}
