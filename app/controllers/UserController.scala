package controllers

import helpers.TypeHelpers._
import models.QuoteQueries
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._
import play.api.libs.json.Json


object UserController extends Controller {
  implicit def dataSource = DB.getDataSource()

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

  def vote(id: String, up: Boolean) = Action { implicit request =>
    val action = QuoteQueries.updateRating(if (up) 1 else -1) _
    parseLong(id).map(action) match {
      case Some(rating) =>
        val data = Map("rating" -> rating)
        val json = Json.toJson(data)
        Ok(json)
      case _            => NotFound("Not found")
    }
  }
}