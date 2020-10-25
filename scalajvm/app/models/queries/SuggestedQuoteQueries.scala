package models.queries

import java.time.{ZonedDateTime, ZoneOffset}

import models.data.SuggestedQuote
import scalikejdbc._

case class SuggestedQuoteQueries()(implicit session: DBSession) {

  def insertSuggestedQuote(content: String, submitterIp: String, source: String): Long = {
    val q = SuggestedQuote.column
    withSQL {
      insert.into(SuggestedQuote).columns(q.time, q.content, q.submitterIp, q.source).values(ZonedDateTime.now(ZoneOffset.UTC), content, submitterIp, source)
    }.updateAndReturnGeneratedKey().apply()
  }

  def countSuggestedQuote(): Int = {
    val sq = SuggestedQuote.syntax("sq")
    withSQL {
      select(sqls.count).from(SuggestedQuote as sq)
    }.map(rs => rs.int(1)).first().apply().getOrElse(0)
  }

  def getAllSuggestedQuotes: List[SuggestedQuote] = {
    val sq = SuggestedQuote.syntax("sq")
    withSQL {
      select.from(SuggestedQuote as sq)
    }.map(SuggestedQuote(sq.resultName)).list().apply()
  }

  def getSuggestedQuoteById(id: Long): Option[SuggestedQuote] = {
    val sq = SuggestedQuote.syntax("sq")
    withSQL {
      select.from(SuggestedQuote as sq).where.eq(sq.id, id)
    }.map(SuggestedQuote(sq.resultName)).first().apply()
  }

  def getSuggestedQuoteByToken(token: String): Option[SuggestedQuote] = {
    val sq = SuggestedQuote.syntax("sq")
    withSQL {
      select.from(SuggestedQuote as sq).where.eq(sq.token, token)
    }.map(SuggestedQuote(sq.resultName)).first().apply()
  }

  def deleteSuggestedQuoteByToken(token: String): Unit = {
    val sq = SuggestedQuote.column

    withSQL {
      delete.from(SuggestedQuote).where.eq(sq.token, token)
    }.update().apply()
  }
}
