import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import scalikejdbc._


import models.data.{StagedQuote, DisabledAction, Maintainer}
import models.queries.{StagedQuoteQueries, DisabledActionQueries}

import ru.org.codingteam.loglist.dto.StagedQuoteDTO

class QuoteStagingDisableSpec extends Specification {

  def clearTable[T](table : SQLSyntaxSupport[T])(implicit db: DBSession): Unit =
    withSQL { deleteFrom(table) }.update().apply()

  "The quote stage operation" should {
    "stage the quote when the operation is enabled and return StagedQuoteDTO as JSON" in {
      running(FakeApplication()) {
        DB localTx { implicit session =>
          clearTable(DisabledAction)
          clearTable(StagedQuote)
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
          clearTable(DisabledAction)
          clearTable(StagedQuote)

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

    "disable itself when it cannot free some space in the staging area " +
      "after exceeding certain limit" in {
      running(FakeApplication(additionalConfiguration = Map("stage.countLimit" -> 5))) {
        DB localTx { implicit session =>
          clearTable(DisabledAction)
          clearTable(StagedQuote)
          clearTable(Maintainer)
        }

        val request = FakeRequest(
          Helpers.POST,
          controllers.api.routes.Quotes.stageQuote().url,
          FakeHeaders(),
          "Hello, World"
        )

        for (i <- 1 to 5) { status(route(request).get) mustEqual 200 }
        status(route(request).get) mustEqual 503

        1 mustEqual 1
      }
    }
  }
}
