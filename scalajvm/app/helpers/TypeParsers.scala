package helpers

object TypeParsers {
  def parseLong(s: String): Option[Long] = try {
    Some(java.lang.Long.parseLong(s))
  } catch {
    case _ : java.lang.NumberFormatException => None
  }

  def parseInt(s: String): Option[Int] = try {
    Some(java.lang.Integer.parseInt(s))
  } catch {
    case _ : java.lang.NumberFormatException => None
  }
}
