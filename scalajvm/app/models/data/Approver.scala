package models.data

import scalikejdbc._

case class Approver(id: Long, name: String, email: String)
object Approver extends SQLSyntaxSupport[Approver] {
  override val tableName = "approver"
  def apply(rs: WrappedResultSet): Approver =
    new Approver(rs.long("id"), rs.string("name"), rs.string("email"))
}
