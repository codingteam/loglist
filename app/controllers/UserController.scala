package controllers

import scala.math._
import data.Data
import helpers.TypeHelpers._
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._


object UserController extends Controller {
  implicit def dataSource = DB.getDataSource()

  def index = Action { implicit request =>
    val countQuotes = Data.countQuotes
    val pageSize = 50
    val countPages = (countQuotes + pageSize - 1) / pageSize

    val pageNumber: Long =
      request
        .getQueryString("page")
        .flatMap(parseLong)
        .flatMap(x => if (0 <= x && x < countPages) Some(x) else None)
        .getOrElse(0)
    val quotes = Data.getPageOfQuotes(pageNumber, pageSize)

    Ok(views.html.index(quotes, pageNumber, countPages))
  }

  def quote(idString: String) = Action { implicit request =>
    parseLong(idString).flatMap(Data.getQuoteById) match {
      case Some(quote) => Ok(views.html.quote(quote))
      case _           => NotFound("Not found")
    }
  }
}