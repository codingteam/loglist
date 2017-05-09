package controllers.api

import cors.Cors
import models.queries.VotingQueries
import play.api.mvc._
import ru.org.codingteam.loglist.QuoteRating

object Voting extends Controller with Cors {
  def vote(id: Long, up: Boolean) = corsActionWithTx { request =>
    import request.dbSession

    val rating = VotingQueries().updateRating(if (up) 1 else -1)(id)
    val data = QuoteRating(rating)
    val json = upickle.write(data)
    Ok(json).as("application/json")
  }
}
