package models.queries

import models.data.Approver
import scalikejdbc._

case class ApproverQueries(implicit session: DBSession) {
  def getAllApprovers: List[Approver] = {
    val a = Approver.syntax("a")
    withSQL {
      select(a.*).from(Approver as a)
    }.map(rs => Approver(rs)).list().apply()
  }

  def insertApprover(name: String, email: String): Long = {
    val a = Approver.column
    withSQL {
      insert.into(Approver).columns(a.name, a.email).values(name, email)
    }.updateAndReturnGeneratedKey().apply()
  }

  def getApproverById(id: Long): Option[Approver] = {
    val a = Approver.syntax("a")
    withSQL {
      select(a.*).from(Approver as a).where.eq(a.id, id)
    }.map(rs => Approver(rs)).first().apply()
  }
}
