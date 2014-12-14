package models.queries

import models.data.SuggestedQuote
import org.joda.time.DateTime
import scalikejdbc._

case class SuggestedQuoteQueries(implicit session: DBSession) {

  def insertQueuedQuote(content: String, submitterIp: Option[String]): Long = {
    val q = SuggestedQuote.column
    withSQL {
      insert.into(SuggestedQuote).columns(q.time, q.content, q.submitterIp).values(DateTime.now(), content, submitterIp)
    }.updateAndReturnGeneratedKey().apply()
  }

  def getAllQueuedQuotes: List[SuggestedQuote] = {
    val sq = SuggestedQuote.syntax("sq")
    withSQL {
      select(sq.*).from(SuggestedQuote as sq)
    }.map(rs => SuggestedQuote(rs)).list().apply()
  }

  def getQueuedQuoteById(id: Long): Option[SuggestedQuote] = {
    val sq = SuggestedQuote.syntax("sq")
    withSQL {
      select(sq.*).from(SuggestedQuote as sq).where.eq(sq.id, id)
    }.map(rs => SuggestedQuote(rs)).first().apply()
  }

  def getQueuedQuoteByToken(token: String): Option[SuggestedQuote] = {
    val sq = SuggestedQuote.syntax("sq")
    withSQL {
      select(sq.*).from(SuggestedQuote as sq).where.eq(sq.token, token)
    }
  }.map(rs => SuggestedQuote(rs)).first().apply()

  def deleteQueuedQuoteByToken(token: String): Unit = {
    val sq = SuggestedQuote.column

    withSQL {
      delete.from(SuggestedQuote).where.eq(sq.token, token)
    }.update().apply()
  }
}
