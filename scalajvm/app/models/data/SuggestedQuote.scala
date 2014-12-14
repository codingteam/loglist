package models.data

import org.joda.time.DateTime
import scalikejdbc._

case class SuggestedQuote(id: Long, time: DateTime,
                          content: String, submitterIp: Option[String],
                          token: String)

object SuggestedQuote extends SQLSyntaxSupport[SuggestedQuote] {
  override val tableName = "queued_quote"
  def apply(rs: WrappedResultSet) =
    new SuggestedQuote(rs.long("id"), rs.jodaDateTime("time"),
      rs.string("content"), rs.stringOpt("submitter_ip"),
      rs.string("token"))
}
