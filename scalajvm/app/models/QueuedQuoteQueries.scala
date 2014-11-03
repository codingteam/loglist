package models

import org.joda.time.DateTime
import scalikejdbc._

object QueuedQuoteQueries {
  implicit val session = AutoSession

  def insertQueuedQuote(content: String, submitterIp: Option[String]): Unit = {
    val q = QueuedQuote.column
    withSQL {
      insert.into(QueuedQuote).columns(q.time, q.content, q.submitterIp).values(DateTime.now(), content, submitterIp)
    }.update().apply()
  }
}
