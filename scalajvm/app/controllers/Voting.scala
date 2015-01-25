package controllers

import helpers.ActionWithTx
import helpers.TypeParsers._
import models.queries.VotingQueries
import play.api.Play.current
import play.api.mvc._
import scalikejdbc._
import ru.org.codingteam.loglist.QuoteRating

object Voting extends Controller {
  def vote(id: Long, up: Boolean) = ActionWithTx { request =>
    import request.dbSession

    val rating = VotingQueries().updateRating(if (up) 1 else -1)(id)
    val data = QuoteRating(rating)
    val json = upickle.write(data)
    Ok(json).as("application/json")
  }
}
