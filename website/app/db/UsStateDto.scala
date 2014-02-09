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
          select id, name, abbr
          from us_state;"""

        Logger.info("UsStateDto.getAll():" + query)

        SQL(query)().map(row =>
          new UsState(
            row[Long]("id"),
            row[String]("name"),
            row[String]("abbr")
          )
        ).toList
    }
  }

  def getOfId(id: Long): UsState = {
    DB.withConnection {
      implicit c =>

        val query = """
          select id, name, abbr
          from us_state
          where id = '""" + id + """';"""

        Logger.info("UsStateDto.getOfId():" + query)

        val firstRow = SQL(query).apply().head

        new UsState(
          firstRow[Long]("id"),
          firstRow[String]("name"),
          firstRow[String]("abbr")
        )
    }
  }
}
