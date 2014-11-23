package models.queries

import models.data.Quote
import scalikejdbc._

object VotingQueries {
  implicit val session = AutoSession

  def updateRating(increment: Int)(id: Long): Int = {
    val q = Quote.column
    DB localTx { implicit session =>
      withSQL {
        update(Quote).set(
          q.rating -> sqls"${q.rating} + $increment"
        ).where.eq(q.id, id)
          .append(sqls"returning ${q.rating}")
      }.map(rs => rs.int(1)).first().apply().getOrElse(0)
    }
  }
}
