package controllers.api

import cors.CorsController
import models.data.PostQuoteRequest
import models.queries.{ApiKeyQueries, SuggestedQuoteQueries}
import play.api.mvc._
import play.api.Configuration
import ru.org.codingteam.loglist.QuoteCount
import scalikejdbc.DBSession
import services.SuggestedQuoteService
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import javax.inject._

@Singleton
class SuggestedQuotes @Inject()(implicit cc: ControllerComponents, configuration: Configuration) extends CorsController(cc, configuration) {
  def countSuggestedQuotes(): Action[AnyContent] = corsActionWithTx { request =>
    import request.dbSession
    val data = QuoteCount(SuggestedQuoteQueries().countSuggestedQuote())
    val json = data.asJson.noSpaces
    Ok(json).as("application/json")
  }

  def newQuote(): Action[AnyContent] = corsActionWithTx { implicit request =>
    val json = request.body.asJson
    val string = json.toString()
    val decoded = decode[PostQuoteRequest](string).toOption
    decoded match {
      case None =>
        BadRequest
      case Some(quote) =>
        import request.dbSession
        insertSuggestedQuote(request.remoteAddress, quote)
    }
  }

  private def insertSuggestedQuote(remoteAddress: String, quote: PostQuoteRequest)
                                  (implicit request: RequestHeader, session: DBSession): Result = {
    ApiKeyQueries().getApiKeyByKey(quote.apiKey) match {
      case None => NotFound
      case Some(key) =>
        SuggestedQuoteService.insertAndNotify(quote.text, remoteAddress, key.source)
        Ok
    }
  }
}
