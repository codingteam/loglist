package helpers

import com.netaporter.uri.dsl._
import dispatch._, Defaults._
import play.api.Play.current
import play.libs.Json

import scala.concurrent.Future

object ReCaptcha {

  lazy val publicKey = current.configuration.getString("recaptcha.publickey").get
  lazy val privateKey = current.configuration.getString("recaptcha.privatekey").get

  val verificationUrl = "https://www.google.com/recaptcha/api/siteverify"

  def check(addr: String, response: String): Future[Boolean] = {
    val uri = verificationUrl ? ("secret" -> privateKey) & ("response" -> response) & ("remoteip" -> addr)
    val request = url(uri)
    val result = Http(request OK as.String)
    result.map { res => Json.parse(res).get("success").asBoolean() } // TODO: Proper error handling, check recaptcha documentation
  }

}