import helpers.DatabaseHelpers
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import play.api.Play.current

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

    "should return 507 in case count limit is exceeded" in {
      running(FakeApplication()) {
        DB localTx { implicit session =>
          clearTable(StagedQuote)
        }

        val stagingText = "Hello, World"

        for (i <- 1 to 5) { status(route(stageRequestForText(stagingText)).get) mustEqual 200 }
        status(route(stageRequestForText(stagingText)).get) mustEqual 507
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
