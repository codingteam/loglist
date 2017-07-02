package controllers

import helpers.{ActionWithTx, ReCaptcha}
import models.forms.SuggestQuoteForm
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import services.SuggestedQuoteService

import scala.concurrent.Await
import scala.concurrent.duration._

object SuggestedQuotes extends Controller {
  def newQuote() = Action { request =>
    Ok(views.html.newQuote(quoteForm))
  }

  def addQuote() = ActionWithTx { implicit request =>
    import request.dbSession

    quoteForm.bindFromRequest.fold(

      formWithErrors => {
        BadRequest(views.html.newQuote(formWithErrors))
      },
      form => {
        val remoteAddress = request.remoteAddress
        if (Await.result(ReCaptcha.check(remoteAddress, form.reCapthaResponse), 30.seconds)) { // TODO: Async check
          SuggestedQuoteService.insertAndNotify(form.content, remoteAddress, "user")
          Ok(views.html.quoteSuggested())
        } else {
          BadRequest(views.html.newQuote(quoteForm.bind(form.toMap))) // TODO: Report captcha error to user
        }
      }
    )
  }

  private val quoteForm = Form(
    mapping(
      "content" -> nonEmptyText,
      "g-recaptcha-response" -> nonEmptyText
    )(SuggestQuoteForm.apply)(SuggestQuoteForm.unapply)
  )
}
