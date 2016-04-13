package controllers

import helpers.ActionWithTx
import play.api.mvc._

object Licenses extends Controller {
  def listLicenses() = Action {
      Ok(views.html.licenses())
  }
}
