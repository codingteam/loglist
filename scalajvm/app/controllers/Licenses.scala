package controllers

import play.api.mvc._
import javax.inject._

@Singleton
class Licenses @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  def listLicenses() = Action {
      Ok(views.html.licenses())
  }
}
