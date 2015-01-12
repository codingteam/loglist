package models.forms

case class SuggestQuoteForm(content: String, reCapthaResponse: String) {
  def toMap = Map("content" -> content)
}
