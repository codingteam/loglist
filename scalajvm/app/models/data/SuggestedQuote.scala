package models.data

import java.time.ZonedDateTime

import scalikejdbc._

case class SuggestedQuote(id: Long, source: String, time: ZonedDateTime,
                          content: String, submitterIp: Option[String],
                          token: String)

object SuggestedQuote extends SQLSyntaxSupport[SuggestedQuote] {
  override val tableName = "suggested_quote"
  def apply(g: ResultName[SuggestedQuote])(rs: WrappedResultSet) =
    new SuggestedQuote(
      id = rs.get(g.id),
      source = rs.get(g.source),
      time = rs.get(g.time),
      content = rs.get(g.content),
      submitterIp = rs.get(g.submitterIp),
      token = rs.get(g.token))
}
