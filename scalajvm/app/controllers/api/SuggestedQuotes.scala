package controllers.api

import cors.Cors
import models.data.PostQuoteRequest
import models.queries.{ApiKeyQueries, SuggestedQuoteQueries}
import play.api.mvc._
import ru.org.codingteam.loglist.QuoteCount
import scalikejdbc.DBSession
import services.SuggestedQuoteService
import upickle.Invalid

object SuggestedQuotes extends Controller with Cors {
  def countSuggestedQuotes(): Action[AnyContent] = corsActionWithTx { request =>
    import request.dbSession
    val data = QuoteCount(SuggestedQuoteQueries().countSuggestedQuote())
    val json = upickle.write(data)
    Ok(json).as("application/json")
  }

  def newQuote(): Action[AnyContent] = corsActionWithTx { implicit request =>
    request.body.asJson map (_.toString()) flatMap tryReadJson[PostQuoteRequest] match {
      case None =>
        BadRequest
      case Some(quote) =>
        import request.dbSession
        insertSuggestedQuote(request.remoteAddress, quote)
    }
  }

  private def tryReadJson[T](body: String)(implicit reader: upickle.Reader[T]): Option[T] = {
    try {
      Some(upickle.read[T](body))
    } catch {
      case e: Invalid => None
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
