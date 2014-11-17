package models.queries

import models.data.SuggestedQuote
import org.joda.time.DateTime
import scalikejdbc._

object SuggestedQuoteQueries {
  implicit val session = AutoSession

  def insertQueuedQuote(content: String, submitterIp: Option[String]): Unit = {
    val q = SuggestedQuote.column
    withSQL {
      insert.into(SuggestedQuote).columns(q.time, q.content, q.submitterIp).values(DateTime.now(), content, submitterIp)
    }.update().apply()
  }
}
