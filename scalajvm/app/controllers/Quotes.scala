package controllers

import javax.sql.DataSource
import helpers.TypeParsers._
import models.queries.QuoteQueries
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._
import security.BasicAuth

object Quotes extends Controller {
  implicit def dataSource: DataSource = DB.getDataSource()

  def index = Action { implicit request =>
    val countQuotes = QuoteQueries.countQuotes
    val pageSize = 50
    val countPages = (countQuotes + pageSize - 1) / pageSize

    val pageNumber: Int =
      request
        .getQueryString("page")
        .flatMap(parseInt)
        .flatMap(x => if (0 <= x && x < countPages) Some(x) else None)
        .getOrElse(0)
    val quotes = QuoteQueries.getPageOfQuotes(pageNumber, pageSize)

    Ok(views.html.index(quotes, pageNumber, countPages))
  }

  def quote(idString: String) = Action { implicit request =>
    parseLong(idString).flatMap(QuoteQueries.getQuoteById) match {
      case Some(quote) => Ok(views.html.quote(quote))
      case _           => NotFound("Not found")
    }
  }

  def addQuote = BasicAuth {
    Action { implicit request =>
      request.body.asText match {
        case Some(content) => {
          QuoteQueries.insertQuote(content)
          Ok("The quote has been added")
        }

        case _ =>
          BadRequest("The request body was not found!")
      }
    }
  }

  def deleteQuote(idString: String) = BasicAuth {
    Action { implicit request =>
      parseLong(idString).flatMap(id => Some(QuoteQueries.removeQuoteById(id))) match {
        case Some(true) => Ok(s"The quote $idString has been deleted")
        case _          => NotFound(s"There is no quote with id $idString")
      }
    }
  }
}
