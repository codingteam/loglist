package models

import org.joda.time.DateTime
import scalikejdbc._

object QueuedQuoteQueries {
  implicit val session = AutoSession

  def insertQueuedQuote(content: String, author: Option[String]): Unit = {
    val q = QueuedQuote.column
    withSQL {
      insert.into(QueuedQuote).columns(q.time, q.content, q.author).values(DateTime.now(), content, author)
    }.update().apply()
  }
}
