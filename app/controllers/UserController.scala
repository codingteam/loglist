package controllers

import java.sql.Timestamp
import javax.sql.DataSource

import data.Data
import play.api._
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._

object UserController extends Controller {

  implicit def dataSource = DB.getDataSource()

  def index = Action {
    Ok(views.html.index(Data.getAllQuotes))
  }

  def quote(id: Long) = Action { implicit request =>
    val quote = Data.getQuoteById(id)
    if(quote.isDefined)
      Ok(views.html.quote(quote))
    else
      NotFound("Not found")
  }


}