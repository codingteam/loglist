package models.data

import org.joda.time.DateTime
import scalikejdbc._

class StagedQuote(id: Long, token: String, content: String, time: DateTime, stagerIp: String)
object StagedQuote extends SQLSyntaxSupport[StagedQuote] {
  override val tableName = "staged_quote"
  def apply(rs: WrappedResultSet) =
    new StagedQuote(rs.long("id"), rs.string("token"),
      rs.string("content"), rs.jodaDateTime("time"),
      rs.string("stager_ip"))
}