package controllers

import data.Data
import helpers.TypeHelpers._
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._


object UserController extends Controller {
  implicit def dataSource = DB.getDataSource()

  def index = Action {
    Ok(views.html.index(Data.getAllQuotes))
  }

  def quote(idString: String) = Action { implicit request =>
    parseLong(idString).flatMap(Data.getQuoteById) match {
      case Some(quote) => Ok(views.html.quote(quote))
      case _           => NotFound("Not found")
    }
  }
}