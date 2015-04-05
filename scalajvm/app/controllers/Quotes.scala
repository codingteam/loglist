package controllers

import helpers.ActionWithTx
import models.queries.{QuoteFilter, QuoteOrdering, QuoteQueries}
import play.api.Play.current
import play.api.mvc._
import security.BasicAuth
import scalikejdbc._

object Quotes extends Controller {

  def list(page: Int, order: QuoteOrdering.Value, filter: QuoteFilter.Value) = ActionWithTx { request =>
    import request.dbSession
    val countQuotes = QuoteQueries().countQuotes(order, filter)
    val pageSize = 50
    val countPages = (countQuotes + pageSize - 1) / pageSize

    val pageNumber = if (0 <= page && page < countPages) page else 0
    val quotes = QuoteQueries().getPageOfQuotes(pageNumber, pageSize, order, filter)

    Ok(views.html.index(quotes, pageNumber, countPages, order, filter))
  }

  def quote(id: Long) = ActionWithTx { request =>
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
