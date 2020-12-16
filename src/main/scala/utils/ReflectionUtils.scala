package utils

import com.google.common.reflect.{ ClassPath, TypeToken}

import scala.collection.mutable
import scala.collection.JavaConverters._

/**
 * @author: Romit Srivastava
 * @since: 8/4/20 14:22
 * @vreion: 1.0.0
 */

object ReflectionUtils {
  private[this] lazy val CLASS_PATH = ClassPath.from(this.getClass.getClassLoader)

  def implementationClasses[T](clazz: Class[T], packageName: String): Seq[Class[T]] =
    getClasses[T](packageName).filter { c =>
      !c.isInterface && TypeToken
        .of(c)
        .getTypes
        .asScala
        .exists(t => t.getRawType == clazz)

    }.toSeq


  def getClasses[T](packageName: String): mutable.Set[Class[T]] = CLASS_PATH.getTopLevelClassesRecursive(packageName).asScala.map(_.load().asInstanceOf[Class[T]])


}
