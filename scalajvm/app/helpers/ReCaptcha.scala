package helpers

import play.libs.Json
import play.api.Configuration
import javax.inject._
import scalaj.http._

import scala.concurrent.{Future, blocking}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ReCaptcha @Inject()(implicit configuration: Configuration) {

  lazy val publicKey = configuration.get[String]("recaptcha.publickey")
  lazy val privateKey = configuration.get[String]("recaptcha.privatekey")

  val verificationUrl = "https://www.google.com/recaptcha/api/siteverify"

  def check(addr: String, response: String): Future[Boolean] = {
    val request = Http(verificationUrl).params("secret" -> privateKey, "response" -> response, "remoteip" -> addr)
    val result = Future { blocking { request.asString.body } }
    result.map { res => Json.parse(res).get("success").asBoolean() } // TODO: Proper error handling, check recaptcha documentation
  }

}
