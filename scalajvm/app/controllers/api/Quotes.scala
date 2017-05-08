package controllers.api

import helpers.ActionWithTx
import models.queries.{QuoteFilter, QuoteOrdering, QuoteQueries}
import models.data.Quote
import play.api.mvc._
import ru.org.codingteam.loglist.dto.QuoteDTO
import ru.org.codingteam.loglist.QuoteCount

object Quotes extends Controller {
  def getQuote(id: Long) = ActionWithTx { request =>
    import request.dbSession
    prepareResponse(QuoteQueries().getQuoteById(id))
  }

  def getCount(order: QuoteOrdering.Value, filter: QuoteFilter.Value) =
    ActionWithTx { request =>
      import request.dbSession
      val count = QuoteQueries().countQuotes(order, filter)
      val response = QuoteCount(count)
      json(upickle.write(response))
    }

  def getRandomQuote = ActionWithTx { request =>
    import request.dbSession
    prepareResponse(QuoteQueries().getRandomQuote)
  }

  private def prepareResponse(probablyQuote: Option[Quote]) =
    probablyQuote.map(buildQuoteDto) match {
      case Some(quoteDTO) => json(upickle.write(quoteDTO))
      case None           => NotFound("").as("text/plain")
    }

  private def buildQuoteDto(quote: Quote): QuoteDTO =
    QuoteDTO(quote.id, quote.time.getMillis, quote.content.getOrElse(""), quote.rating)

  private def json(text: String) = Ok(text).as("application/json; charset=utf-8")
}
