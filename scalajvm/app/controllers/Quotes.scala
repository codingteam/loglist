package controllers

import helpers.ActionWithTx
import models.data.FeedItem
import models.queries.{QuoteFilter, QuoteOrdering, QuoteQueries}
import play.api.Play.current
import play.api.mvc._
import scala.xml.{Atom, PCData}
import scalikejdbc._
import security.BasicAuth

object Quotes extends Controller {

  def list(page: Int, order: QuoteOrdering.Value, filter: QuoteFilter.Value) = ActionWithTx { request =>
    import request.dbSession
    val countQuotes = QuoteQueries().countQuotes(filter)
    val pageSize = 50
    val countPages = (countQuotes + pageSize - 1) / pageSize

    val pageNumber = if (0 <= page && page < countPages) page else 0
    val quotes = QuoteQueries().getPageOfQuotes(pageNumber, pageSize, order, filter)

    Ok(views.html.index(quotes, pageNumber, countPages, order, filter))
  }

  def getRSSFeed = ActionWithTx { implicit request =>
    import request.dbSession
    import play.api.Play.current

    val feedLimit = current.configuration.getInt("feed.limit").get

    val quotes = QuoteQueries().getPageOfQuotes(0, feedLimit,
        QuoteOrdering.Time, QuoteFilter.None)
    val items = quotes.map(quote => {
        val description = quote.content getOrElse ""
        val content =
          scala.xml.Utility.escape(description)
          .replaceAll("[\n\r]+", "<br/>")

        val itemUrl = routes.Quotes.quote(quote.id).absoluteURL()

        new FeedItem(
          quote.time,
          "Цитата №" + quote.id.toString(),
          content,
          itemUrl,
          // Using quote's URL as its GUID, which is a common practice
          itemUrl
          )})

    val deploymentURL =
      routes.Quotes.list(0, QuoteOrdering.Time, QuoteFilter.None).absoluteURL()
    val feed = views.xml.rssFeed(
        items,
        "LogList: последние цитаты",
        deploymentURL,
        "Охапка свежайших цитат с " + deploymentURL
      )

    Ok(feed).as("application/rss+xml")
  }

  def quote(id: Long) = ActionWithTx { implicit request =>
    import request.dbSession
    QuoteQueries().getQuoteById(id) match {
      case Some(quote) => Ok(views.html.quote(quote))
      case _           => NotFound("Not Found")
    }
  }

  def addQuote = BasicAuth {
    ActionWithTx { request =>
      import request.dbSession
      request.body.asText match {
        case Some(content) => {
          QuoteQueries().insertQuote(content)
          Ok("The quote has been added")
        }

        case _ =>
          BadRequest("The request body was not found!")
      }
    }
  }

  def deleteQuote(id: Long) = BasicAuth {
    ActionWithTx { request =>
      import request.dbSession
      if (QuoteQueries().removeQuoteById(id)) Ok(s"The quote $id has been deleted")
      else NotFound(s"There is no quote with id $id")
    }
  }
}
