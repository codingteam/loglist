package helpers

import javax.mail.{Transport, Message, Session, MessagingException}
import javax.mail.internet.{InternetAddress, MimeMessage}

import controllers.routes
import models.data.{SuggestedQuote, Approver}
import java.util.Properties

import play.api.Play.current
import play.api.mvc.RequestHeader

object Notifications {
  private val approvalSmtpHost = current.configuration.getString("approval.smtpHost").get
  private val approvalEmail = current.configuration.getString("approval.email").get
  private val approvalEmailPassword = current.configuration.getString("approval.emailPassword").get

  def notifyApproversAboutSuggestedQuote(approvers: List[Approver], suggestedQuote: SuggestedQuote)
                                        (implicit request: RequestHeader) = {
    val properties = new Properties
    properties.put("mail.smtp.host", approvalSmtpHost)
    properties.put("mail.smtp.starttls.enable", "true")

    val session = Session.getDefaultInstance(properties)

    try {
      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(approvalEmail))
      approvers.foreach(a => message.addRecipients(Message.RecipientType.TO, a.email))
      message.setSubject("LogList Notification: suggested quote!")
      message.setText(s"Please check the quote here: ${routes.Approving.getApprovalForm(suggestedQuote.token).absoluteURL()}")
      Transport.send(message, approvalEmail, approvalEmailPassword)
    } catch {
      case e: MessagingException => e.printStackTrace()
    }
  }
}

