package db

import play.api.db.DB
import play.api.Logger
import anorm._
import play.api.Play.current
import models.TitleOfAnOfficial

object TitleOfAnOfficialDto {
  def getAll: List[TitleOfAnOfficial] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select id, name
          from title_of_an_official;"""

        Logger.info("TitleOfAnOfficialDto.getAll():" + query)

        SQL(query)().map(row =>
          new TitleOfAnOfficial(
            row[Long]("id"),
            row[String]("name")
          )
        ).toList
    }
  }
}
