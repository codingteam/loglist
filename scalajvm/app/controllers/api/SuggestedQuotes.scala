package controllers.api

import cors.Cors
import models.queries.SuggestedQuoteQueries
import play.api.mvc.{Action, AnyContent, Controller}
import ru.org.codingteam.loglist.QuoteCount

object SuggestedQuotes extends Controller with Cors {
  def countSuggestedQuotes(): Action[AnyContent] = corsActionWithTx { request =>
    import request.dbSession
    val data = QuoteCount(SuggestedQuoteQueries().countSuggestedQuote())
    val json = upickle.write(data)
    Ok(json).as("application/json")
  }
}
