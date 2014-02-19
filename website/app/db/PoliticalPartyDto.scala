package db

import play.api.db.DB
import play.api.Logger
import anorm._
import play.api.Play.current
import models.PoliticalParty

object PoliticalPartyDto {
  def getAll: List[PoliticalParty] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select id, name
          from political_parties;"""

        Logger.info("PoliticalPartyDto.getAll():" + query)

        SQL(query)().map(row =>
          new PoliticalParty(
            row[Long]("id"),
            row[String]("name")
          )
        ).toList
    }
  }
}
