package controllers.api

import cors.CorsController
import models.queries.{QuoteFilter, QuoteOrdering, QuoteQueries}
import models.data.Quote
import play.api.mvc._
import play.api.Configuration
import io.circe.generic.auto._
import io.circe.syntax._
import ru.org.codingteam.loglist.dto.QuoteDTO
import ru.org.codingteam.loglist.QuoteCount
import javax.inject._

@Singleton
class Quotes @Inject()(implicit cc: ControllerComponents, configuration: Configuration) extends CorsController(cc, configuration) {
  def getQuote(id: Long) = corsActionWithTx { request =>
    import request.dbSession
    prepareResponse(QuoteQueries().getQuoteById(id))
  }

  def getRandomQuote = corsActionWithTx { request =>
    import request.dbSession
    prepareResponse(QuoteQueries().getRandomQuote)
  }

  def getCount(filter: QuoteFilter.Value) = corsActionWithTx { request =>
    import request.dbSession
    val count = QuoteQueries().countQuotes(filter)
    val response = QuoteCount(count)
    json(response.asJson.noSpaces)
  }

  def getList(limit: Int, page: Int, order: QuoteOrdering.Value, filter: QuoteFilter.Value) =
    corsActionWithTx { request =>
      import request.dbSession

      val pageSize = if (0 < limit && limit <= 1000) limit else 50
      val pageNumber = if (0 <= page) page else 0
      val quotes = QuoteQueries()
        .getPageOfQuotes(pageNumber, pageSize, order, filter)
        .map(buildQuoteDto)

      json(quotes.asJson.noSpaces)
    }

  private def prepareResponse(probablyQuote: Option[Quote]) =
    probablyQuote.map(buildQuoteDto) match {
      case Some(quoteDTO) => json(quoteDTO.asJson.noSpaces)
      case None           => NotFound("").as("text/plain")
    }

  private def buildQuoteDto(quote: Quote): QuoteDTO =
    QuoteDTO(quote.id, quote.source, quote.sourceUrl.orNull, quote.time.getMillis, quote.content.getOrElse(""), quote.rating)

  private def json(text: String) = Ok(text).as("application/json; charset=utf-8")
}
