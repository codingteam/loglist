package global

import helpers.Cors
import play.api.mvc._

object Options extends Controller {
  def corsSupport(url: String) = Action { request =>
    NoContent.withHeaders(Cors.headers(request.headers): _*)
  }
}
