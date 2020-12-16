import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers, Suite}

trait TestingSuite extends FunSuite with BeforeAndAfterAll with  Matchers{

  self: Suite =>

  var sparkTest: SparkSession = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    sparkTest = SparkSession.builder().appName("Test Suite").master("local").getOrCreate()
  }

  override protected def afterAll(): Unit = {
    sparkTest.stop()
    super.afterAll()
  }

}
