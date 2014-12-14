package models.queries

import helpers.BindableEnumeration
import models.data.Quote
import org.joda.time.DateTime
import scalikejdbc._

object QuoteOrdering extends BindableEnumeration {
  val Time, Rating = Value

  def toSQL(provider: QuerySQLSyntaxProvider[SQLSyntaxSupport[Quote], Quote],
            value: QuoteOrdering.Value): SQLSyntax = {
    value match {
      case QuoteOrdering.Time => provider.time
      case QuoteOrdering.Rating => provider.rating
    }
  }
}

object QuoteFilter extends BindableEnumeration {
  val None, Year, Month, Week, Day = Value

  def toSQL(provider: QuerySQLSyntaxProvider[SQLSyntaxSupport[Quote], Quote],
            value: QuoteFilter.Value): Option[SQLSyntax] = {
    val today = DateTime.now().withTimeAtStartOfDay()
    val periodStart = value match {
      case QuoteFilter.None => Option.empty[DateTime]
      case QuoteFilter.Year => Some(today.withDayOfYear(1))
      case QuoteFilter.Month => Some(today.withDayOfMonth(1))
      case QuoteFilter.Week => Some(today.withDayOfWeek(1))
      case QuoteFilter.Day => Some(today)
    }

    sqls.toAndConditionOpt(periodStart.map { period => sqls.ge(provider.time, period)})
  }
}

case class QuoteQueries(implicit session: DBSession) {

  def getPageOfQuotes(pageNumber: Int,
                      pageSize: Int,
                      order: QuoteOrdering.Value,
                      filter: QuoteFilter.Value): Seq[Quote] = {
    val q = Quote.syntax("q")

    withSQL {
      select(
        q.*
      ).from(Quote as q)
        .where(QuoteFilter.toSQL(q, filter))
        .orderBy(QuoteOrdering.toSQL(q, order)).desc
        .offset(pageNumber * pageSize)
        .limit(pageSize)
    }.map(rs => Quote(rs)).list().apply()
  }

  def countQuotes(ordering: QuoteOrdering.Value,
                  filter: QuoteFilter.Value): Int = {
    val q = Quote.syntax("q")
    withSQL {
      select(sqls.count)
        .from(Quote as q)
        .where(QuoteFilter.toSQL(q, filter))
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
