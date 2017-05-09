package controllers.api

import helpers.{ActionWithTx, Cors, RequestWithSession}
import models.queries.{QuoteFilter, QuoteOrdering, QuoteQueries}
import models.data.Quote
import play.api.mvc._
import ru.org.codingteam.loglist.dto.QuoteDTO
import ru.org.codingteam.loglist.QuoteCount

object Quotes extends Controller {
  private def action(responseAction: RequestWithSession[AnyContent] => Result) = ActionWithTx { request =>
    val response = responseAction(request)
    val headers = Cors.headers(request.headers)
    response.withHeaders(headers: _*)
  }

  def getQuote(id: Long) = action { request =>
    import request.dbSession
    prepareResponse(QuoteQueries().getQuoteById(id))
  }

  def getRandomQuote = action { request =>
    import request.dbSession
    prepareResponse(QuoteQueries().getRandomQuote)
  }

  def getCount(filter: QuoteFilter.Value) = action { request =>
      import request.dbSession
      val count = QuoteQueries().countQuotes(filter)
      val response = QuoteCount(count)
      json(upickle.write(response))
    }

  def getList(limit: Int, page: Int, order: QuoteOrdering.Value, filter: QuoteFilter.Value) = action { request =>
      import request.dbSession

      val pageSize = if (0 <= limit && limit <= 1000) limit else 50
      val pageNumber = if (0 <= page) page else 0
      val quotes = QuoteQueries()
        .getPageOfQuotes(pageNumber, pageSize, order, filter)
        .map(buildQuoteDto)

      json(upickle.write(quotes))
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
