package models.queries

import models.data.ApiKey
import scalikejdbc._

case class ApiKeyQueries(implicit session: DBSession) {

  def getApiKeyByKey(key: String): Option[ApiKey] = {
    val ak = ApiKey.syntax("ak")
    withSQL {
      select(ak.*).from(ApiKey as ak).where.eq(ak.key, key)
    }
  }.map(rs => ApiKey(rs)).first().apply()
}
