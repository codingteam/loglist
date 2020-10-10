package models.data

import scalikejdbc._

case class Approver(id: Long, name: String, email: String)
object Approver extends SQLSyntaxSupport[Approver] {
  override val tableName = "approver"
  def apply(a: ResultName[Approver])(rs: WrappedResultSet): Approver =
    new Approver(rs.long(a.id), rs.string(a.name), rs.string(a.email))
}
