package models

import scalikejdbc._
import org.joda.time.DateTime

case class Quote(id: Long, time: DateTime, content: Option[String])
object Quote extends SQLSyntaxSupport[Quote] {
  override val tableName = "quote"
  def apply(rs: WrappedResultSet): Quote =
    new Quote(rs.long("id"), rs.jodaDateTime("time"), rs.stringOpt("content"))
}
