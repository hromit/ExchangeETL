package streams

import com.johnsnowlabs.nlp.annotators.sbd.pragmatic.SentenceDetector
import com.johnsnowlabs.nlp.annotators.sda.pragmatic.SentimentDetector
import com.johnsnowlabs.nlp.annotators.sda.vivekn.{ViveknSentimentApproach, ViveknSentimentModel}
import com.johnsnowlabs.nlp.annotators.{Normalizer, Tokenizer}
import com.johnsnowlabs.nlp.{DocumentAssembler, Finisher, SparkNLP}
import utils.SparkConfigurations
import com.johnsnowlabs.nlp.pretrained.PretrainedPipeline
import com.johnsnowlabs.util.Benchmark
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.NGram

import org.apache.spark.sql.functions.lit


object SparkNLPTestMain extends SparkConfigurations{
  override val extraConfigurations: Traversable[(String, String)] = Map("spark.app.name" -> "TestS3Connection",
    "spark.serializer" -> "org.apache.spark.serializer.KryoSerializer")

  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir","C:\\winutils")


    val df = spark.read.text("C:\\Users\\91801\\Downloads\\chromedriver_win32\\MoneyControlTopGainers2020-06-13T17-57-20-893Z.html")

   df.show(20)

    //converting to columns by splitting
    import spark.implicits._
    val df2 = df.map(f=>{
      val elements = f.toString()
     // (elements(0), elements(1), elements(2))
      elements
    })
   // df2.printSchema()
   // df2.show(2000,false)
   //  df2.withColumn("aaa", lit("jj"))

    val document = new DocumentAssembler()
      .setInputCol("value")
      .setOutputCol("document")


    val token = new Tokenizer()
      .setInputCols("document")
      .setOutputCol("token")


    val sentenceDetector = new SentenceDetector()
      .setInputCols("document")
      .setOutputCol("sentence")

   /* val sentimentDetector =new SentimentDetector()
      .setInputCols(Array("document","token"))
      .setOutputCol("sentiment")*/

     //val sentmodel : ViveknSentimentModel = ViveknSentimentModel.load("C:\\Users\\91801\\Downloads\\sentiment_vivek")

    val sentimentDetector = new ViveknSentimentApproach()
      .setInputCols(Array("token", "sentence"))
      .setOutputCol("vivekn")
      .setSentimentCol("value")
      .setCorpusPrune(1)

   /* val normalizer = new Normalizer()
      .setInputCols("token")
      .setOutputCol("normal")

    val finisher = new Finisher()
      .setInputCols("normal")*/

   /* val ngram = new NGram()
      .setN(3)
      .setInputCol("finished_normal")
      .setOutputCol("3-gram")

    val gramAssembler = new DocumentAssembler()
      .setInputCol("3-gram")
      .setOutputCol("3-grams")*/

    val pipeline = new Pipeline().setStages(Array(document, token ,sentenceDetector, sentimentDetector))

  /*

    val sentimentDetector =new SentimentDetector()
     .setInputCols(Array("token", "sentence"))
     .setOutputCol("sentiment")

    val pipeline = new Pipeline().setStages(Array(document, token, sentenceDetector, sentimentDetector))*/



    val result = pipeline.fit(df2).transform(df2)
    Benchmark.time("Time to convert and show") {result.show(truncate=false)}

   /* val pipe = PretrainedPipeline("explain_document_ml", lang="en")
    pipe.annotate("ddddd")
    val annotation = pipe.transform(df2.toDF())

    annotation.show()*/




    //  df.show(10, false)

   /* println(SparkNLP.version())


    val explainDocumentPipeline = PretrainedPipeline("explain_document_dl_noncontrib")
    val result = explainDocumentPipeline.annotate("Harry Potter is a great movie.")

    println(result("spell"))*/


  }

}
