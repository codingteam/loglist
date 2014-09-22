package controllers

import security.BasicAuth
import controllers.UserController._
import data.Data

import play.api._
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._

object AdminController extends Controller {
  implicit def dataSource = DB.getDataSource()

  def addQuote = BasicAuth {
    Action { implicit request =>
      request.body.asText match {
        case Some(content) => {
          Data.insertQuote(content)
          Ok("The quote has been added")
        }

        case _ =>
          BadRequest("The request body was not found!")
      }
    }
  }

  def deleteQuote(id: Long) = BasicAuth {
    Action { implicit request =>
      if (Data.removeQuoteById(id))
        Ok(s"The quote $id has been deleted")
      else
        NotFound(s"There is no quote with id $id")
    }
  }
}
