package utils

/**
 * @author: Romit Srivastava
 * @since 11/4/20 13:41
 * @version; 1.0.0
 */

import com.google.common.base.CaseFormat
import com.google.common.base.Converter

object StringUtils {

  lazy val underscoreConverter: Converter[String,String] = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE)
  lazy val lowlandersConverter: Converter[String,String] = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL)

  implicit class StringWrapper(s: String) {

    def toUnderscore: String = underscoreConverter.convert(s)

    def toLowerCamel: String = lowlandersConverter.convert(s)

    def placeholderReplaceBy(placeholder: String, replacement: Any*): String = {
      val list = s.split(placeholder).zip(replacement.toSeq)
      val buffer = new StringBuilder()
      list.foreach {
        case (s,p: String) =>
          buffer.append(s).append(s"'$p'")
        case (s,p) =>
          buffer.append(s).append(p)
      }
      buffer.toString()
    }
  }

}
