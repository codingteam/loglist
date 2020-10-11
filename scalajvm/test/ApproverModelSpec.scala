import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

import scalikejdbc._

class ApproverModelSpec extends Specification {
  val approvers = List(
    ("rexim", "rexim@loglist.xyz"),
    ("ForNeVeR", "fornever@loglist.xyz")
  )

  "The Approvers model" should {
    "be able to add new approvers and return them back" in {
      running(FakeApplication()) {
        DB localTx { implicit session =>
          for ((name, email) <- approvers) {
            models.queries.ApproverQueries().insertApprover(name, email)
          }
          models.queries.ApproverQueries().getAllApprovers must have size approvers.size
        }
      }

    }
  }
}
