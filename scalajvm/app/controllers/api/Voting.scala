package controllers.api

import cors.CorsController
import models.queries.VotingQueries
import play.api.mvc._
import play.api.Configuration
import io.circe.generic.auto._
import io.circe.syntax._
import ru.org.codingteam.loglist.QuoteRating
import javax.inject._

@Singleton
class Voting @Inject()(implicit cc: ControllerComponents, configuration: Configuration) extends CorsController(cc, configuration) {
  def vote(id: Long, up: Boolean) = corsActionWithTx { request =>
    import request.dbSession

    val rating = VotingQueries().updateRating(if (up) 1 else -1)(id)
    val data = QuoteRating(rating)
    val json = data.asJson.noSpaces
    Ok(json).as("application/json")
  }
}
