package models.data

import scalikejdbc._

case class ApiKey(id: Long, key: String, source: String, sourceUrl: Option[String])

object ApiKey extends SQLSyntaxSupport[ApiKey] {
  override val tableName = "api_key"
  def apply(n: ResultName[ApiKey])(rs: WrappedResultSet) =
    new ApiKey(rs.long(n.id), rs.string(n.key), rs.string(n.source), rs.stringOpt(n.sourceUrl))
}
