package models.data

import org.joda.time.DateTime
import scalikejdbc._

case class Quote(id: Long, time: DateTime, content: Option[String], rating: Int)
object Quote extends SQLSyntaxSupport[Quote] {
  override val tableName = "quote"
  def apply(rs: WrappedResultSet): Quote =
    new Quote(rs.long("id"), rs.jodaDateTime("time"), rs.stringOpt("content"), rs.int("rating"))
}
