package controllers

import models.QuoteQueries
import security.BasicAuth
import helpers.TypeHelpers._

import play.api.Play.current
import play.api.db.DB
import play.api.mvc._

object AdminController extends Controller {
  implicit def dataSource = DB.getDataSource()

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
