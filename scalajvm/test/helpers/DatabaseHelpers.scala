package helpers

import scalikejdbc._

trait DatabaseHelpers {
  def clearTable[T](table : SQLSyntaxSupport[T])(implicit db: DBSession): Unit =
    withSQL { deleteFrom(table) }.update().apply()
}
