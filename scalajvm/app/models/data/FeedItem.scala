package models.data

import org.joda.time.DateTime
import scala.xml._

case class FeedItem(time: DateTime, title: String, description: String, link: String, guid: String)
