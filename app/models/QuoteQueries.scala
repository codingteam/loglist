package models

import org.joda.time._
import scalikejdbc._

object QuoteQueries {
  implicit val session = AutoSession

  def getPageOfQuotes(pageNumber: Int, pageSize: Int, best: String): Seq[Quote] = {
    val q = Quote.syntax("q")

    val order = best match {
      case "none" => q.time
      case "time" => q.time
      case _ => q.rating
    }

    val range = best match {
      case "year" => DateTime.now().minusYears(1)
      case "month" => DateTime.now().minusMonths(1)
      case "week" => DateTime.now().minusWeeks(1)
      case "day" => DateTime.now().minusDays(1)
      case _ => new DateTime(0)
    }

    withSQL {
      select(
        q.*
      ).from(Quote as q)
       .where.between(q.time, range, DateTime.now())
       .orderBy(order).desc
       .offset(pageNumber * pageSize)
       .limit(pageSize)
    }.map(rs => Quote(rs)).list().apply()
  }

  def countQuotes(): Int = {
    val q = Quote.syntax("q")
    withSQL {
      select(sqls.count).from(Quote as q)
    }.map(rs => rs.int(1)).first().apply().getOrElse(0)
  }

  def getQuoteById(id: Long): Option[Quote] = {
    val q = Quote.syntax("q")
    withSQL {
      select(
        q.*
      ).from(Quote as q).where.eq(q.id, id)
    }.map(rs => Quote(rs)).first().apply()
  }

  def insertQuote(content: String): Unit = {
    val q = Quote.column
    withSQL {
      insert.into(Quote).columns(q.content, q.time).values(content, DateTime.now())
    }.update().apply()
  }

  def removeQuoteById(id: Long): Boolean = {
    val q = Quote.column
    withSQL {
      delete.from(Quote).where.eq(q.id, id)
    }.update().apply() != 0
  }

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
