package streams

import java.io.File

import org.jsoup.Jsoup
import org.jsoup.nodes._
import org.jsoup.select._
import org.jsoup._

import scala.collection.mutable.ListBuffer

object TestJSOUP extends App {

 // val file: File  = new File("C:\\Users\\91801\\Downloads\\chromedriver_win32\\MoneyControlFront2020-06-13T19-36-47-308Z.html")
 // val file: File  = new File("C:\\Users\\91801\\Downloads\\chromedriver_win32\\MoneyControlTopGainers2020-06-14T19-59-42-651Z.html")
 val file: File  = new File("C:\\Users\\91801\\Downloads\\chromedriver_win32\\MoneyControlHourlyGainersPageProcessor2020-06-14T21-34-15-55Z.html")



  val loadFile = Jsoup.parse(file,null)

   val elements: Elements =  loadFile.select("table")

  import scala.collection.JavaConversions._

  var topCompanies: ListBuffer[String] = ListBuffer()


  for ( element <- elements){
    if(element.child(0).tagName().equals("thead")) {
      val rows: Elements = element.select("tr")
      for (row <- rows){
        val tds: Elements = row.select("td")
        for(td <- tds){
        //  println(td)
          topCompanies += td.text()
        }
       // if(element.child(0).tagName().equals("td").){
       topCompanies += "||"//}
      }
    }
  }

  //topCompanies.foreach(println)
  println("====PRINTING OUTPUT IN STRING ===")
  println(topCompanies.toString())

//  println(loadFile.body().toString)



}
