package models.data

import scalikejdbc._

case class DisabledAction(name: String)
object DisabledAction extends SQLSyntaxSupport[DisabledAction] {
  override val tableName = "disabled_action"
  def apply(rs: WrappedResultSet): DisabledAction =
    new DisabledAction(rs.string("name"))
}
