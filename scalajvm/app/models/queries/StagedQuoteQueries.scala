package models.queries

import models.data.StagedQuote
import org.joda.time.DateTime
import scalikejdbc._

case class StagedQuoteQueries(implicit session: DBSession) {
  def getStagedQuoteByToken(token: String): Option[StagedQuote] = {
    val sq = StagedQuote.syntax("sq")
    withSQL {
      select(sq.*).from(StagedQuote as sq).where.eq(sq.token, token)
    }.map(rs => StagedQuote(rs)).first().apply()
  }

  def getStagedQuoteById(id: Long): Option[StagedQuote] = {
    val sq = StagedQuote.syntax("sq")
    withSQL {
      select(sq.*).from(StagedQuote as sq).where.eq(sq.id, id)
    }.map(rs => StagedQuote(rs)).first().apply()
  }

  def insertStagedQuote(content: String, stagerIp: Option[String]) = {
    val sq = StagedQuote.column
    withSQL {
      insert.into(StagedQuote).columns(sq.time, sq.content, sq.stagerIp).values(DateTime.now(), content, stagerIp)
    }.updateAndReturnGeneratedKey().apply()
  }
}
