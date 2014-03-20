package db

import play.api.db.DB
import play.api.Logger
import anorm._
import play.api.Play.current
import models.UsState

object UsStateDto {
  def getAll: List[UsState] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select id, name
          from us_state;"""

        Logger.info("UsStateDto.getAll():" + query)

        SQL(query)().map {
          row =>
            new UsState(
              row[String]("id"),
              row[String]("name")
            )
        }.toList
    }
  }

  def getOfId(id: String): Option[UsState] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select name
          from us_state
          where id = '""" + id + """';"""

        Logger.info("UsStateDto.getOfId():" + query)

        SQL(query).apply().headOption match {
          case Some(row) =>
            Some(
              UsState(
                id,
                row[String]("name")
              )
            )
          case None => None
        }
    }
  }
}
