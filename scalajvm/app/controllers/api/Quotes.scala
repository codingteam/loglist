package controllers.api

import helpers.ActionWithTx
import models.queries.QuoteQueries
import models.data.Quote
import play.api.mvc._
import ru.org.codingteam.loglist.dto.QuoteDTO

object Quotes extends Controller {
  def getQuote(id: Long) = ActionWithTx { request =>
    import request.dbSession
    prepareResponse(QuoteQueries().getQuoteById(id))
  }

  def getRandomQuote = ActionWithTx { request =>
    import request.dbSession
    prepareResponse(QuoteQueries().getRandomQuote)
  }

  private def prepareResponse(probablyQuote: Option[Quote]) =
    probablyQuote.map(buildQuoteDto) match {
      case Some(quoteDTO) => Ok(upickle.write(quoteDTO)).as("application/json; charset=utf-8")
      case None           => NotFound("").as("text/plain")
    }

  private def buildQuoteDto(quote: Quote): QuoteDTO =
    QuoteDTO(quote.id, quote.time.getMillis, quote.content, quote.rating)
}
