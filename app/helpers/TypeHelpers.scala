package helpers

object TypeHelpers {
  def parseLong(s: String): Option[Long] = try {
    Some(java.lang.Long.parseLong(s))
  } catch {
    case _ : java.lang.NumberFormatException => None
  }
}
