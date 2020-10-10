package models.queries

import models.data.Quote
import scalikejdbc._

case class VotingQueries()(implicit session: DBSession) {
  def updateRating(increment: Int)(id: Long): Int = {
    val q = Quote.column
    withSQL {
      update(Quote).set(
        q.rating -> sqls"${q.rating} + $increment"
      ).where.eq(q.id, id)
        .append(sqls"returning ${q.rating}")
    }.map(rs => rs.int(1)).first().apply().getOrElse(0)
  }
}
