package utils

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.Try

import java.util.concurrent.TimeUnit
/**
 * @author: Romit Srivastava
 * @since: 8/4/20 14:38
 * @version: 1.0.0
 *
 */

object RetryUtils {

  def futureRetryWhen[T](invoke: => Future[T], retryTime: Int = 0, retryInfo: RetryInfo = RetryInfo()) (implicit context:  ExecutionContext = ExecutionContext.Implicits.global): Future[T] =
    invoke.recoverWith{
      case ex if retryInfo.exceptions.contains(ex.getClass.getName) && retryTime > 0 =>
        TimeUnit.MILLISECONDS.sleep(retryInfo.duration.toMillis)
        futureRetryWhen(invoke, retryTime -1, retryInfo)(context)
      case other =>
        throw other
    }


  def retryWhen[T](invoke: => T, retryTime: Int = 0, retryInfo: RetryInfo = RetryInfo()): T =
    Try(invoke).recover{
      case ex: Throwable if retryInfo.exceptions.contains(ex.getClass.getName) && retryTime > 0 =>
        TimeUnit.MILLISECONDS.sleep(retryInfo.duration.toMillis)
        retryWhen(invoke, retryTime -1, retryInfo)
      case other =>
        throw other
    }.get

  

  case class RetryInfo(duration: Duration = 1 seconds, exceptions: Seq[String] = Seq(classOf[Throwable].getName))

}
