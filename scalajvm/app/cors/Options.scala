package cors

import play.api.mvc._
import play.api.Configuration
import javax.inject._

@Singleton
class Options @Inject()(cc: ControllerComponents, configuration: Configuration) extends CorsController(cc, configuration) {
  def corsSupport(url: String) = Action { request =>
    NoContent.withHeaders(corsHeaders(request.headers): _*)
  }
}
