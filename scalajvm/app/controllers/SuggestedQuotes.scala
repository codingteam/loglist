package controllers

import helpers.{ActionWithTx, ReCaptcha}
import models.forms.SuggestQuoteForm
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api.i18n
import play.api.Configuration
import services.SuggestedQuoteService
import javax.inject._

import scala.concurrent.Await
import scala.concurrent.duration._

@Singleton
class SuggestedQuotes @Inject()(implicit cc: ControllerComponents, configuration: Configuration) extends AbstractController(cc) with i18n.I18nSupport {
  def newQuote() = Action { implicit request =>
    Ok(views.html.newQuote(quoteForm))
  }

  def addQuote() = (new ActionWithTx(cc)) { implicit request =>
    import request.dbSession

    quoteForm.bindFromRequest().fold(

      formWithErrors => {
        BadRequest(views.html.newQuote(formWithErrors))
      },
      form => {
        val remoteAddress = request.remoteAddress
        if (Await.result((new ReCaptcha).check(remoteAddress, form.reCapthaResponse), 30.seconds)) { // TODO: Async check
          SuggestedQuoteService.insertAndNotify(form.content, remoteAddress, "user")
          Ok(views.html.quoteSuggested())
        } else {
          BadRequest(views.html.newQuote(quoteForm.bind(form.toMap))) // TODO: Report captcha error to user
        }
      }
    )
  }

  override def messagesApi: i18n.MessagesApi = cc.messagesApi

  private val quoteForm = Form(
    mapping(
      "content" -> nonEmptyText,
      "g-recaptcha-response" -> nonEmptyText
    )(SuggestQuoteForm.apply)(SuggestQuoteForm.unapply)
  )
}
