import helpers.DatabaseHelpers
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import scalikejdbc._

import models.data.StagedQuote
import models.queries.StagedQuoteQueries
import ru.org.codingteam.loglist.dto.StagedQuoteDTO

class StagingSpec extends Specification with DatabaseHelpers {

  def stageRequestForText(text: String) = FakeRequest(
    Helpers.POST,
    controllers.api.routes.Quotes.stageQuote().url,
    FakeHeaders(),
    text
  )

  "The quote stage operation" should {
    "stage the quote and return StagedQuoteDTO as JSON" in {
      running(FakeApplication()) {
        DB localTx { implicit session =>
          clearTable(StagedQuote)
        }

        val request = route(stageRequestForText("Hello, World")).get

        val dto = upickle.read[StagedQuoteDTO](contentAsString(request))
        val stagedQuote = DB localTx { implicit session =>
          StagedQuoteQueries().getStagedQuoteByToken(dto.token).get
        }

        dto.token mustEqual stagedQuote.token
        dto.content mustEqual stagedQuote.content
        dto.time mustEqual stagedQuote.time.getMillis
      }
    }
  }

  "The submit form page" should {
    "contain the staged quote if you provide appropriate stagedQuoteToken" in {
      running(FakeApplication()) {
        DB localTx { implicit session =>
          clearTable(StagedQuote)
        }

        val expectedText = "Foo, Bar"

        val stageResponse = route(stageRequestForText(expectedText)).get
        val dto = upickle.read[StagedQuoteDTO](contentAsString(stageResponse))

        val submitFormResponse = controllers.SuggestedQuotes.newQuote(dto.token)(FakeRequest())

        contentAsString(submitFormResponse) must contain(expectedText)
      }
    }
  }
}
