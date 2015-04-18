package controllers

import helpers.{ActionWithTx, Notifications, ReCaptcha}
import models.forms.SuggestQuoteForm
import models.queries._
import models.forms._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import ru.org.codingteam.loglist.SuggestedQuoteCount

import scala.concurrent.Await
import scala.concurrent.duration._

object SuggestedQuotes extends Controller {

  def countSuggestedQuotes() = ActionWithTx { implicit request =>
    import request.dbSession
    val data = SuggestedQuoteCount(SuggestedQuoteQueries().countSuggestedQuote())
    val json = upickle.write(data)
    Ok(json).as("application/json")
  }

  def newQuote(stagedQuoteToken: String) = ActionWithTx { request =>
    import request.dbSession
    val content = StagedQuoteQueries().getStagedQuoteByToken(stagedQuoteToken).map(_.content).getOrElse("")
    Ok(views.html.newQuote(quoteForm.fill(SuggestQuoteForm(content, ""))))
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
          val id = SuggestedQuoteQueries().insertSuggestedQuote(form.content, Some(remoteAddress))
          SuggestedQuoteQueries().getSuggestedQuoteById(id) match {
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
      "g-recaptcha-response" -> nonEmptyText
    )(SuggestQuoteForm.apply)(SuggestQuoteForm.unapply)
  )
}
