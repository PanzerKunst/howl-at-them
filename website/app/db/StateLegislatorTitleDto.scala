package db

import play.api.db.DB
import play.api.Logger
import anorm._
import play.api.Play.current
import models.StateLegislatorTitle

object StateLegislatorTitleDto {
  def getAll: List[StateLegislatorTitle] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select id, name
          from state_legislator_titles;"""

        Logger.info("StateLegislatorTitleDto.getAll():" + query)

        SQL(query)().map(row =>
          new StateLegislatorTitle(
            row[Long]("id"),
            row[String]("name")
          )
        ).toList
    }
  }
}
