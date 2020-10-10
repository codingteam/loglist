package security

import play.api.Logging
import java.util.Base64
import play.api.mvc._
import scala.concurrent.Future
import play.mvc.Results._
import play.api.Configuration

// Stolen from here: http://www.mentful.com/2014/06/14/basic-authentication-filter-for-play-framework/
case class BasicAuth[A](action: Action[A])(implicit configuration: Configuration) extends Action[A] with Logging {
  private lazy val unauthResult = Results.Unauthorized.withHeaders(("WWW-Authenticate",
    "Basic realm=\"LogList\""))
  //need the space at the end
  private lazy val basicSt = "basic "

  //This is needed if you are behind a load balancer or a proxy
  private def getUserIPAddress(request: Request[A]): String = {
    request.headers.get("x-forwarded-for").getOrElse(request.remoteAddress.toString)
  }

  private def logFailedAttempt(request: Request[A]) = {
    logger.warn(s"IP address ${getUserIPAddress(request)} failed to log in, " +
      s"requested uri: ${request.uri}")
  }

  private def decodeBasicAuth(auth: String): Option[(String, String)] = {
    if (auth.length() < basicSt.length()) {
      return None
    }
    val basicReqSt = auth.substring(0, basicSt.length())
    if (basicReqSt.toLowerCase != basicSt) {
      return None
    }
    val basicAuthSt = auth.replaceFirst(basicReqSt, "")
    val decoder = Base64.getDecoder()
    val decodedAuthSt = new String(decoder.decode(basicAuthSt), "UTF-8")
    val usernamePassword = decodedAuthSt.split(":")
    if (usernamePassword.length >= 2) {
      //account for ":" in passwords
      return Some(usernamePassword(0), usernamePassword.splitAt(1)._2.mkString)
    }
    None
  }

  def apply(request: Request[A]): Future[Result] = {
    val result = for (
      username     <- configuration.getOptional[String]("basicAuth.username");
      password     <- configuration.getOptional[String]("basicAuth.password");
      basicAuth    <- request.headers.get("authorization");
      (user, pass) <- decodeBasicAuth(basicAuth) if username == user && password == pass
    ) yield action(request)

    result.getOrElse {
      logFailedAttempt(request)
      Future.successful(unauthResult)
    }
  }

  def executionContext = scala.concurrent.ExecutionContext.global

  lazy val parser = action.parser
}
