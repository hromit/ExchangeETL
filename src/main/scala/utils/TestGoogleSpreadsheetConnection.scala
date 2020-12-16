package utils

import com.johnsnowlabs.nlp.annotators.{Normalizer, Tokenizer}
import com.johnsnowlabs.nlp.{DocumentAssembler, Finisher, SparkNLP}
import org.apache.spark.sql.functions._
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{NGram, StopWordsRemover}
import com.johnsnowlabs.util.Benchmark
import org.apache.spark.sql.DataFrame


object TestGoogleSpreadsheetConnection extends SparkConfigurations{
  override val extraConfigurations: Traversable[(String, String)] = Map("spark.app.name" -> "TestS3Connection",
       "spark.serializer" -> "org.apache.spark.serializer.KryoSerializer")


  def main(args: Array[String]): Unit = {

    import spark.implicits._

    // Creates a DataFrame from a specified worksheet
    val df = spark.read.
      format("com.github.potix2.spark.google.spreadsheets").
      option("serviceAccountId", "romit-479@pitaara-7f5b4.iam.gserviceaccount.com").
      option("credentialPath", "C:\\Users\\91801\\Downloads\\pitaara-7f5b4-9a88c70a2c02.p12").
      load("1-GhK27KRMHkWFCPKQIZQfMaNMoGN2s2u7QNvNz-9SKU/TestSpark")

    df.filter(col("Detected state")==="Kollam").show(2000)

    val df1 = df.filter(col("Detected District")==="Gautam Buddha Nagar").select("Backup Notes").na.drop()

    println(SparkNLP.version())



    val document = new DocumentAssembler()
      .setInputCol("Backup Notes")
      .setOutputCol("document")

    val token = new Tokenizer()
      .setInputCols("document")
      .setOutputCol("token")

   /* val stoWordRemover = new StopWordsRemover()
       .setInputCol("token")
       .setOutputCol("stopw")*/

    val normalizer = new Normalizer()
      .setInputCols("token")
      .setOutputCol("normal")

    val finisher = new Finisher()
      .setInputCols("normal")

    val ngram = new NGram()
      .setN(3)
      .setInputCol("finished_normal")
      .setOutputCol("3-gram")

    val gramAssembler = new DocumentAssembler()
      .setInputCol("3-gram")
      .setOutputCol("3-grams")

    val pipeline = new Pipeline().setStages(Array(document, token, normalizer, finisher, ngram, gramAssembler))

   /* val testing = Seq(
      (1, "Google is a famous company"),
      (2, "Peter Parker is a super heroe")
    ).toDS().toDF( "_id", "text")*/


  //  val dd : DataFrame= Seq.empty[String].toDS.toDF("text")
    val result = pipeline.fit(df1).transform(df1)
    Benchmark.time("Time to convert and show") {result.show(truncate=false)}

    /* df.printSchema()

     println(df.filter(col("Date Announced")==="28/03/2020").count())

     val fromResponsesDF = spark.read.
       format("com.github.potix2.spark.google.spreadsheets").
       option("serviceAccountId", "romit-479@pitaara-7f5b4.iam.gserviceaccount.com").
       option("credentialPath", "C:\\Users\\91801\\Downloads\\pitaara-7f5b4-9a88c70a2c02.p12").
       load("1-GhK27KRMHkWFCPKQIZQfMaNMoGN2s2u7QNvNz-9SKU/FromResponses")

     fromResponsesDF.filter(col("I want to report a")==="Death")show(1000)
 */
  }

}
