package models.queries

import models.data.DisabledAction

import scalikejdbc._

case class DisabledActionQueries(implicit session: DBSession) {
  def isActionDisabled(name: String): Boolean = {
    val da = DisabledAction.syntax("da")
    withSQL {
      select(da.*).from(DisabledAction as da).where.eq(da.name, name)
    }.map(rs => DisabledAction(rs)).first().apply().isDefined
  }

  def disableAction(name: String): Unit = {
    val da = DisabledAction.column
    if (!isActionDisabled(name)) withSQL {
      insert.into(DisabledAction).columns(da.name).values(name)
    }.update().apply()
  }

  def enableAction(name: String): Unit = {
    val da = DisabledAction.column
    if (isActionDisabled(name)) withSQL {
      delete.from(DisabledAction).where.eq(da.name, name)
    }.update().apply()
  }
}
