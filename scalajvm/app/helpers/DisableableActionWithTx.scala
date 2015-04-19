package helpers

import models.queries.DisabledActionQueries
import play.api.mvc._
import scalikejdbc._

import scala.concurrent._
import ExecutionContext.Implicits.global

case class DisableableActionWithTx(name: String, message: String) extends ActionBuilder[RequestWithSession] with Results {
  override def invokeBlock[A](request: Request[A], block: RequestWithSession[A] => Future[Result]): Future[Result] = {
    DB localTx { implicit session =>
      if (DisabledActionQueries().isActionDisabled(name)) Future(ServiceUnavailable(message).as("text/plain"))
      else block(new RequestWithSession[A](request))
    }
  }
}
