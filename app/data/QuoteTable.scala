package data

import scala.slick.driver.H2Driver.simple._
import java.sql.Timestamp

class QuoteTable(tag: Tag) extends Table[(Long, Timestamp, String)](tag, "QUOTE") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def time = column[Timestamp]("TIME")
  def content = column[String]("CONTENT")

  def * = (id, time, content)
}