package models.data

import scalikejdbc._

case class ApiKey(id: Long, key: String, source: String, sourceUrl: Option[String])

object ApiKey extends SQLSyntaxSupport[ApiKey] {
  override val tableName = "api_key"
  def apply(rs: WrappedResultSet) =
    new ApiKey(rs.long("id"), rs.string("key"), rs.string("source"), rs.stringOpt("source_url"))
}
