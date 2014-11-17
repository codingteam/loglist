package controllers

import javax.sql.DataSource

import helpers.TypeParsers._
import models.queries.VotingQueries
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._
import ru.org.codingteam.loglist.QuoteRating

object Voting extends Controller {
  implicit def dataSource: DataSource = DB.getDataSource()

  def vote(id: String, up: Boolean) = Action { implicit request =>
    val action = VotingQueries.updateRating(if (up) 1 else -1) _
    parseLong(id).map(action) match {
      case Some(rating) =>
        val data = QuoteRating(rating)
        val json = upickle.write(data)
        Ok(json).as("application/json")
      case _ =>
        NotFound("Not found")
    }
  }
}
