package models.queries

import helpers.BindableEnumeration
import models.data.Quote
import org.joda.time.DateTime
import scalikejdbc._

object QuoteOrdering extends BindableEnumeration {
  val Time, Rating = Value
}

object QuoteFilter extends BindableEnumeration {
  val None, Year, Month, Week, Day = Value
}

object QuoteQueries {
  implicit val session = AutoSession

  def getPageOfQuotes(pageNumber: Int,
                      pageSize: Int,
                      ordering: QuoteOrdering.Value,
                      filter: QuoteFilter.Value): Seq[Quote] = {
    val q = Quote.syntax("q")

    val order = ordering match {
      case QuoteOrdering.Time => q.time
      case QuoteOrdering.Rating => q.rating
    }

    val today = DateTime.now().withTimeAtStartOfDay()
    val periodStart = filter match {
      case QuoteFilter.None => None
      case QuoteFilter.Year => Some(today.withDayOfYear(1))
      case QuoteFilter.Month => Some(today.withDayOfMonth(1))
      case QuoteFilter.Week => Some(today.withDayOfWeek(1))
      case QuoteFilter.Day => Some(today)
    }

    withSQL {
      select(
        q.*
      ).from(Quote as q)
        .where(sqls.toAndConditionOpt(periodStart.map { period => sqls.ge(q.time, period)}))
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
}
