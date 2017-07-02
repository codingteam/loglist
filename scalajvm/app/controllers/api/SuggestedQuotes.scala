package controllers.api

import cors.Cors
import models.data.PostQuoteRequest
import models.queries.SuggestedQuoteQueries
import play.api.mvc.{Action, AnyContent, Controller}
import ru.org.codingteam.loglist.QuoteCount
import upickle.Invalid

object SuggestedQuotes extends Controller with Cors {
  def countSuggestedQuotes(): Action[AnyContent] = corsActionWithTx { request =>
    import request.dbSession
    val data = QuoteCount(SuggestedQuoteQueries().countSuggestedQuote())
    val json = upickle.write(data)
    Ok(json).as("application/json")
  }

  def newQuote(): Action[AnyContent] = corsActionWithTx { request =>
    request.body.asJson map (_.toString()) flatMap tryReadJson[PostQuoteRequest] match {
      case None =>
        BadRequest
      case Some(quote) =>
        Ok(upickle.write(quote)).as("application/json")
    }
  }

  def tryReadJson[T](body: String)(implicit reader: upickle.Reader[T]): Option[T] = {
    try {
      Some(upickle.read[T](body))
    } catch {
      case e: Invalid => None
    }
  }
}
