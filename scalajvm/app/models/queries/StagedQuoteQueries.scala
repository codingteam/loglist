package models.queries

import models.data.StagedQuote
import scalikejdbc._

case class StagedQuoteQueries(implicit session: DBSession) {
  def getStagedQuoteByToken(token: String): Option[StagedQuote] = {
    val sq = StagedQuote.syntax("sq")
    withSQL {
      select(sq.*).from(StagedQuote as sq).where.eq(sq.token, token)
    }.map(rs => StagedQuote(rs)).first().apply()
  }
}
