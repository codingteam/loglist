package models.forms

case class SuggestQuoteForm(content: String, reCaptchaChallenge: String, reCapthaResponse: String) {
  def toMap = Map("content" -> content)
}
