package controllers.api

import helpers.ActionWithTx
import models.queries.QuoteQueries
import play.api.mvc._
import ru.org.codingteam.loglist.dto.QuoteDTO

object Quotes extends Controller {
  def getQuote(id: Long) = ActionWithTx { request =>
    import request.dbSession

    QuoteQueries().getQuoteById(id).map {
      quote => QuoteDTO(quote.id, quote.time.getMillis, quote.content, quote.rating)
    } match {
      case Some(quoteDTO) => Ok(upickle.write(quoteDTO)).as("application/json")
      case None           => NotFound("").as("text/plain")
    }
  }
}
