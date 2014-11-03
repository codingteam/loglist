package models.forms

case class SuggestQuoteData(content: String, reCaptchaChallenge: String, reCapthaResponse: String) {
  def toMap = Map("content" -> content)
}
