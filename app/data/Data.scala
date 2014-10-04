package data

import java.sql.Timestamp
import javax.sql.DataSource

import models.Quote
import org.joda.time.DateTime
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.meta.MTable

import scala.slick.lifted.TableQuery

object Data {

  def quoteTableQuery(implicit session: Session) = {
    val query = TableQuery[QuoteTable]

    if (MTable.getTables(query.baseTableRow.tableName).list.isEmpty) {
      query.ddl.create
    }

    query
  }

  def getAllQuotes(implicit dataSource: DataSource): Seq[Quote] = {
    Database.forDataSource(dataSource) withSession { implicit session =>
      val quotes = for (quote <- quoteTableQuery.sortBy(_.time.desc)) yield (quote.id, quote.time, quote.content)
      quotes.list.map {
        case (id, time, content) => Quote(id, new DateTime(time.getTime), content)
      }
    }
  }

  def getPageOfQuotes(pageNumber: Long, pageSize: Long)(implicit dataSource: DataSource): Seq[Quote] = {
    Database.forDataSource(dataSource) withSession { implicit session =>
      quoteTableQuery.sortBy(_.time.desc).drop(pageNumber * pageSize).take(pageSize).list.map {
        case (id, time, content) => Quote(id, new DateTime(time.getTime), content)
      }
    }
  }

  def countQuotes(implicit dataSource: DataSource): Int = {
    Database.forDataSource(dataSource) withSession { implicit session =>
      quoteTableQuery.size.run
    }
  }

  def getQuoteById(id: Long)(implicit dataSource: DataSource): Option[Quote] = {
    Database.forDataSource(dataSource) withSession { implicit session =>
      quoteTableQuery.filter(_.id === id).firstOption.map {
        case (_, time, content) => Quote(id, new DateTime(time.getTime), content)
      }
    }
  }

  def insertQuote(content: String)(implicit dataSource: DataSource): Unit = {
    Database.forDataSource(dataSource) withSession { implicit session =>
      quoteTableQuery
        .map(q => (q.time, q.content))
        .insert((new Timestamp(DateTime.now.getMillis), content))
    }
  }

  def removeQuoteById(id: Long)(implicit dataSource: DataSource): Boolean = {
    Database.forDataSource(dataSource) withSession { implicit session =>
      quoteTableQuery.filter(_.id === id).delete != 0
    }
  }
}
