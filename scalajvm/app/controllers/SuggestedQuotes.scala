package controllers

import helpers.{ActionWithTx, Notifications, ReCaptcha}
import models.forms.SuggestQuoteForm
import models.queries.{ApproverQueries, SuggestedQuoteQueries}
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import scalikejdbc._

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
        if (ReCaptcha.check(remoteAddress, form.reCaptchaChallenge, form.reCapthaResponse)) {
          val id = SuggestedQuoteQueries().insertQueuedQuote(form.content, Some(remoteAddress))
          SuggestedQuoteQueries().getQueuedQuoteById(id) match {
            case Some(suggestedQuote) => {
              val approvers = ApproverQueries().getAllApprovers
              Notifications.notifyApproversAboutSuggestedQuote(approvers, suggestedQuote)
            }

            case None =>
          }
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
      "recaptcha_challenge_field" -> nonEmptyText,
      "recaptcha_response_field" -> nonEmptyText
    )(SuggestQuoteForm.apply)(SuggestQuoteForm.unapply)
  )
}
