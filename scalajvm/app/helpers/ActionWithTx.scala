package helpers

import play.api.mvc._
import scalikejdbc._

import scala.concurrent.Future

class RequestWithSession[A](request: Request[A])(implicit val dbSession: DBSession) extends WrappedRequest[A](request)

object ActionWithTx extends ActionBuilder[RequestWithSession] {
  override def invokeBlock[A](request: Request[A], block: (RequestWithSession[A]) => Future[Result]): Future[Result] = {
    DB localTx { implicit session =>
      block(new RequestWithSession[A](request))
    }
  }
}
