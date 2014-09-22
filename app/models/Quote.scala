package models

import org.joda.time.DateTime

case class Quote(id: Long, time: DateTime, content: String)
