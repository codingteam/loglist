package models.data

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

case class FeedItem(time: ZonedDateTime, title: String, description: String, link: String, guid: String) {
  def rfcFormattedTime: String = {
    time.format(DateTimeFormatter.RFC_1123_DATE_TIME)
  }
}
