import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.mvc._

import scalikejdbc._

import scala.concurrent.Future

import models.data.{StagedQuote, DisabledAction}
import models.queries.{StagedQuoteQueries, DisabledActionQueries}

import ru.org.codingteam.loglist.dto.StagedQuoteDTO

class QuoteStagingDisableSpec extends Specification {
  "The quote stage operation" should {
    "stage the quote when the operation is enabled and return StagedQuoteDTO as JSON" in {
      running(FakeApplication()) {
        DB localTx { implicit session =>
          withSQL { deleteFrom(DisabledAction) }.update().apply()
          withSQL { deleteFrom(StagedQuote) }.update().apply()
        }

        val request = route(FakeRequest(
          Helpers.POST,
          controllers.api.routes.Quotes.stageQuote().url,
          FakeHeaders(),
          "Hello, World"
        )).get

        val dto = upickle.read[StagedQuoteDTO](contentAsString(request))
        val stagedQuote = DB localTx { implicit session =>
          StagedQuoteQueries().getStagedQuoteByToken(dto.token).get
        }

        dto.token mustEqual stagedQuote.token
        dto.content mustEqual stagedQuote.content
        dto.time mustEqual stagedQuote.time.getMillis
      }
    }

    "return 503 code when the operation is disabled" in {
      running(FakeApplication()) {
        DB localTx { implicit session =>
          withSQL { deleteFrom(DisabledAction) }.update().apply()
          withSQL { deleteFrom(StagedQuote) }.update().apply()

          DisabledActionQueries().disableAction("stage")
        }

        val request = route(FakeRequest(
          Helpers.POST,
          controllers.api.routes.Quotes.stageQuote().url,
          FakeHeaders(),
          "Hello, World"
        )).get

        status(request) mustEqual 503
      }
    }
  }
}
