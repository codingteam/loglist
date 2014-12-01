package models.forms

import helpers.BindableEnumeration

case class ApprovalForm(content: String, token: String, action: String) {
  def toMap = Map("content" -> content, "token" -> token, "action" -> action)
}
