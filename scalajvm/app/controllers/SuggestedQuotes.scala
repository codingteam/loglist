package controllers

import javax.sql.DataSource

import helpers.ReCaptcha
import models.forms.SuggestQuoteForm
import models.queries.SuggestedQuoteQueries
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.db.DB
import play.api.mvc._

object SuggestedQuotes extends Controller {
  implicit def dataSource: DataSource = DB.getDataSource()

  def newQuote() = Action { implicit request =>
    Ok(views.html.newQuote(quoteForm))
  }

  def addQuote() = Action { implicit request =>
    quoteForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.newQuote(formWithErrors))
      },
      form => {
        val remoteAddress = request.remoteAddress
        if (ReCaptcha.check(remoteAddress, form.reCaptchaChallenge, form.reCapthaResponse)) {
          SuggestedQuoteQueries.insertQueuedQuote(form.content, Some(remoteAddress))
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
