package models.data

import java.time.ZonedDateTime

import scalikejdbc._

case class Quote(id: Long, source: String, sourceUrl: Option[String], time: ZonedDateTime, content: Option[String], rating: Int)
object Quote extends SQLSyntaxSupport[Quote] {
  override val tableName = "quote"

  def apply(g: ResultName[Quote])(rs: WrappedResultSet): Quote =
    new Quote(
      id = rs.get(g.id),
      source = rs.get(g.source),
      sourceUrl = rs.get(g.sourceUrl),
      time = rs.get(g.time),
      content = rs.get(g.content),
      rating = rs.get(g.rating))
}
