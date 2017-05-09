package helpers

import global.Options.ORIGIN
import play.api.Play.current
import play.api.mvc.Headers

object Cors {
  private lazy val corsHosts: Set[String] = {
    val config = current.configuration.getString("cors.allowedOrigins")
    config.map(_.split(' ').toSet).getOrElse(Set())
  }

  def headers(requestHeaders: Headers): List[(String, String)] = {
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
}
