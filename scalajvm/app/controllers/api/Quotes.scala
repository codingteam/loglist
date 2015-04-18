package controllers.api

import helpers.ActionWithTx
import models.queries._
import models.data._
import play.api.mvc._
import ru.org.codingteam.loglist.dto.{StagedQuoteDTO, QuoteDTO}

object Quotes extends Controller {

  def getQuote(id: Long) = ActionWithTx { request =>
    import request.dbSession
    prepareResponse(QuoteQueries().getQuoteById(id))
  }

  def getRandomQuote = ActionWithTx { request =>
    import request.dbSession
    prepareResponse(QuoteQueries().getRandomQuote)
  }

  def stageQuote = ActionWithTx { request =>
    import request.dbSession
    request.body.asText match {
      case Some(content) => {
        val id = StagedQuoteQueries().insertStagedQuote(content, Some(request.remoteAddress))
        StagedQuoteQueries().getStagedQuoteById(id) match {
          case Some(stagedQuote) => Ok(upickle.write(buildStagedQuoteDto(stagedQuote))).as("application/json; charset=utf-8")
          case None => InternalServerError("The quote was not staged properly").as("text/plain")
        }
      }
      case None => BadRequest("The request body was not found!").as("text/plain")
    }
  }

  private def prepareResponse(probablyQuote: Option[Quote]) =
    probablyQuote.map(buildQuoteDto) match {
      case Some(quoteDTO) => Ok(upickle.write(quoteDTO)).as("application/json; charset=utf-8")
      case None           => NotFound("").as("text/plain")
    }

  private def buildQuoteDto(quote: Quote): QuoteDTO =
    QuoteDTO(quote.id, quote.time.getMillis, quote.content.getOrElse(""), quote.rating)

  private def buildStagedQuoteDto(stagedQuote: StagedQuote): StagedQuoteDTO =
    StagedQuoteDTO(stagedQuote.token, stagedQuote.content, stagedQuote.time.getMillis)
}
