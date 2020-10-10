package cors

import play.api.http.HeaderNames.ORIGIN
import helpers.{ActionWithTx, RequestWithSession}
import play.api.mvc._
import play.api.Configuration
import javax.inject.Inject

class CorsController @Inject()(cc: ControllerComponents, configuration: Configuration) extends AbstractController(cc) {
  private lazy val corsHosts: Set[String] = {
    val config = configuration.getOptional[String]("cors.allowedOrigins")
    config.map(_.split(' ').toSet).getOrElse(Set())
  }

  protected def corsHeaders(requestHeaders: Headers): List[(String, String)] = {
    requestHeaders.get(ORIGIN) match {
      case Some(origin) if corsHosts.contains(origin) =>
        List (
          "Access-Control-Allow-Origin" -> origin,
          "Access-Control-Allow-Methods" -> "GET, POST, OPTIONS, DELETE, PUT",
          "Access-Control-Max-Age" -> "3600",
          "Access-Control-Allow-Headers" -> "Origin, Content-Type, Accept, Authorization",
          "Access-Control-Allow-Credentials" -> "true"
        )
      case _ => List()
    }
  }

  protected def corsActionWithTx(responseAction: RequestWithSession[AnyContent] => Result)(implicit cc: ControllerComponents) =
    (new ActionWithTx(cc)) { request =>
      val response = responseAction(request)
      val headers = corsHeaders(request.headers)
      response.withHeaders(headers: _*)
    }
}
