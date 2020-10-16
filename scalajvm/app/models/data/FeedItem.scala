package models.data

import java.time.ZonedDateTime

case class FeedItem(time: ZonedDateTime, title: String, description: String, link: String, guid: String)
