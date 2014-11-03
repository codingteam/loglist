package helpers

import net.tanesha.recaptcha.{ReCaptchaFactory, ReCaptchaImpl}
import play.api.Play.current

object ReCaptcha {
  lazy val publicKey = current.configuration.getString("recaptcha.publickey").get
  lazy val privateKey = current.configuration.getString("recaptcha.privatekey").get

  def render(): String = {
    ReCaptchaFactory.newReCaptcha(publicKey, privateKey, false).createRecaptchaHtml(null, "white", 0)
  }

  def check(addr: String, challenge: String, response: String): Boolean = { // TODO: Make this asynchronous
    val reCaptcha = new ReCaptchaImpl()
    reCaptcha.setPrivateKey(privateKey)
    val reCaptchaResponse = reCaptcha.checkAnswer(addr, challenge, response)
    reCaptchaResponse.isValid
  }
}