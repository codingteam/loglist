package helpers

import javax.mail.{Transport, Message, Session, MessagingException}
import javax.mail.internet.{InternetAddress, MimeMessage}

import controllers.routes
import models.data.{SuggestedQuote, Approver, Quote}
import java.util.Properties

import play.api.Configuration
import play.api.mvc.RequestHeader

object Notifications {
  def notifyApproversAboutSuggestedQuote(approvers: List[Approver], suggestedQuote: SuggestedQuote)
                                        (implicit request: RequestHeader,
                                         configuration: Configuration) = {
    try {
      val content = views.html.email.suggestedQuoteNotification(suggestedQuote,
        routes.Approving.getApprovalForm(suggestedQuote.token).absoluteURL()).toString()
      sendApprovalMessage(approvers, subjectFromContent(suggestedQuote.content), content)
    } catch {
      case e: MessagingException => e.printStackTrace()
    }
  }

  def notifyApproversAboutApprovedQuote(approvers: List[Approver],
                                        suggestedQuote: SuggestedQuote,
                                        approvedQuote: Quote)
                                       (implicit request: RequestHeader,
                                        configuration: Configuration) = {
    try {
      val content = views.html.email.approvedQuoteNotification(approvedQuote,
        routes.Quotes.quote(approvedQuote.id).absoluteURL()).toString()
      sendApprovalMessage(approvers, subjectFromContent(suggestedQuote.content), content)
    } catch {
      case e: MessagingException => e.printStackTrace()
    }
  }

  def notifyApproversAboutDeclinedQuote(approvers: List[Approver],
                                        suggestedQuote: SuggestedQuote)
                                       (implicit configuration: Configuration) = {
    try {
      sendApprovalMessage(approvers, subjectFromContent(suggestedQuote.content),
        "<p>This quote has been <b>declined</b></p>")
    } catch {
      case e: MessagingException => e.printStackTrace()
    }
  }

  private def sendApprovalMessage(approvers: List[Approver], subject: String, htmlContent: String)
                                 (implicit configuration: Configuration) = {
    val approvalSmtpHost = configuration.get[String]("approval.smtpHost")
    val approvalEmail = configuration.get[String]("approval.email")
    val approvalEmailPassword = configuration.get[String]("approval.emailPassword")

    val properties = new Properties
    properties.put("mail.smtp.host", approvalSmtpHost)
    properties.put("mail.smtp.starttls.enable", "true")

    val message = new MimeMessage(Session.getInstance(properties))
    message.setFrom(new InternetAddress(approvalEmail))
    approvers.foreach(a => message.addRecipients(Message.RecipientType.TO, a.email))

    message.setSubject(subject, "UTF-8")
    message.setContent(htmlContent, "text/html; charset=utf-8")

    Transport.send(message, approvalEmail, approvalEmailPassword)
  }

  private def subjectFromContent(content: String) =
    s"LogList Suggested Quote: ${elideText(normalizeSpaces(content), 60)}"

  private def normalizeSpaces(text: String) =
    text.split(' ').filter(_.nonEmpty).mkString(" ")

  private def elideText(text: String, maxLength: Int) =
    if (text.length <= maxLength) text
    else s"${text.take(maxLength)}..."
}

