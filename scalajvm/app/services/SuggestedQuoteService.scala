package services

import helpers.Notifications
import models.queries.{ApproverQueries, SuggestedQuoteQueries}
import play.api.mvc.RequestHeader
import play.api.Configuration
import scalikejdbc.DBSession

object SuggestedQuoteService {

  def insertAndNotify(text: String, remoteAddress: String, source: String)
                     (implicit request: RequestHeader, session: DBSession, configuration: Configuration): Unit = {
    val id = SuggestedQuoteQueries().insertSuggestedQuote(text, remoteAddress, source)
    SuggestedQuoteQueries().getSuggestedQuoteById(id) match {
      case Some(suggestedQuote) => {
        val approvers = ApproverQueries().getAllApprovers
        Notifications.notifyApproversAboutSuggestedQuote(approvers, suggestedQuote)
      }

      case None =>
    }
  }
}
