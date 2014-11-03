package models

import scalikejdbc._
import org.joda.time.DateTime

case class QueuedQuote(id: Long, time: DateTime, content: String, author: Option[String])
object QueuedQuote extends SQLSyntaxSupport[QueuedQuote] {
  override val tableName = "queued_quote"
  def apply(rs: WrappedResultSet) =
    new QueuedQuote(rs.long("id"), rs.jodaDateTime("time"), rs.string("content"), rs.stringOpt("author"))
}
