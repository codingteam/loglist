package models.data

import org.joda.time.DateTime
import scala.xml._

case class FeedItem(time: DateTime, title: scala.xml.Atom[String], description: scala.xml.Atom[String], link: String, guid: String)
