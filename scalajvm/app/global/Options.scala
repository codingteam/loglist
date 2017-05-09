package global

import helpers.Cors
import play.api.mvc._

object Options extends Controller with Cors {
  def corsSupport(url: String) = Action { request =>
    NoContent.withHeaders(corsHeaders(request.headers): _*)
  }
}
