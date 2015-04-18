package models.data

import scalikejdbc._

case class Maintainer(id: Long, name: String, email: String)
object Maintainer extends SQLSyntaxSupport[Maintainer] {
  override val tableName = "maintainer"
  def apply(rs: WrappedResultSet): Maintainer =
    new Maintainer(rs.long("id"), rs.string("name"), rs.string("email"))
}
