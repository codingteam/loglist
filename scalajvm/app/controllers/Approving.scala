package controllers

import helpers.{Notifications, ActionWithTx}
import models.forms.{ApprovalForm}
import models.queries._
import play.api.Logging
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api.i18n
import play.api.Configuration
import scalikejdbc._
import security.BasicAuth
import javax.inject._

import scala.concurrent._
import ExecutionContext.Implicits.global

@Singleton
class Approving @Inject()(implicit cc: ControllerComponents, configuration: Configuration) extends AbstractController(cc) with i18n.I18nSupport with Logging {

  def getApprovalForm(token: String) = (new ActionWithTx(cc)) { implicit request =>
    import request.dbSession
    SuggestedQuoteQueries().getSuggestedQuoteByToken(token) match {
      case Some(suggestedQuote) =>
        Ok(views.html.approvalForm(approvalForm.fill(ApprovalForm(suggestedQuote.content, token, ""))))
      case None => NotFound("Suggested quote not found for this token")
    }
  }

  def approve = (new ActionWithTx(cc)) { implicit request =>
    import request.dbSession
    approvalForm.bindFromRequest().fold(
      formWithErrors => BadRequest("Invalid parameters"),

      form => {
        SuggestedQuoteQueries().getSuggestedQuoteByToken(form.token) match {
          case Some(suggestedQuote) => {
            form.action match {
              case "approve" => {
                val approvedQuote = QuoteQueries().getQuoteById(QuoteQueries().insertQuote(form.content, suggestedQuote.source)).get
                val approvers = ApproverQueries().getAllApprovers
                SuggestedQuoteQueries().deleteSuggestedQuoteByToken(form.token)
                notifications.notifyApproversAboutApprovedQuote(approvers, suggestedQuote, approvedQuote)
                Redirect(controllers.routes.Quotes.quote(approvedQuote.id))
              }
              case "decline" => {
                val approvers = ApproverQueries().getAllApprovers
                SuggestedQuoteQueries().deleteSuggestedQuoteByToken(form.token)
                notifications.notifyApproversAboutDeclinedQuote(approvers, suggestedQuote)
                Redirect(controllers.routes.Quotes.list(0, QuoteOrdering.Time, QuoteFilter.None))
              }
              case _ => BadRequest("Invalid parameters")
            }
          }
          case None => BadRequest("Invalid parameters")
        }
      }
    )
  }

  def notifyAllApprovers = BasicAuth {
    Action { implicit request =>
      Future {
        DB localTx { implicit session =>
          val approvers = ApproverQueries().getAllApprovers
          val suggestedQuotes = SuggestedQuoteQueries().getAllSuggestedQuotes
          suggestedQuotes.foreach { quote =>
            notifications.notifyApproversAboutSuggestedQuote(approvers, quote)
            // With hope it won't be treated as SPAM...
            Thread sleep 5000
          }
        }
      }.failed.foreach {
        case e => logger.error(e.getMessage)
      }
      Ok("Notifying all approvers about all suggested quotes...")
    }
  }

  override def messagesApi: i18n.MessagesApi = cc.messagesApi

  private val approvalForm = Form(
    mapping(
      "content" -> nonEmptyText,
      "token" -> nonEmptyText,
      "action" -> nonEmptyText
    )(ApprovalForm.apply)(ApprovalForm.unapply)
  )

  private val notifications = new Notifications
}
