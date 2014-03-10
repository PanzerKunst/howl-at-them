package db

object DbUtil {
  def generateWhereClause(filters: Option[Map[String, String]]) = {
    filters match {
      case Some(filtrs) => {
        filtrs.map {
          case (k, v) => """%s like '%s'""".format(k, safetize(v))
        }
          .mkString("\nwhere ", "\nand ", "")
      }
      case None => ""
    }
  }

  def safetize(string: String): String = {
    string.replaceAll("'", "''")
      .replaceAll("\n", "\\\\n")
  }

  def parseToList[T](string: String): List[T] = {
    val arrayOfString = string.split(',')

    arrayOfString.map {
      item => item.asInstanceOf[T]
    }.toList
  }
}
