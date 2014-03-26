package db

import play.api.db.DB
import play.api.Logger
import anorm._
import play.api.Play.current
import models.Committee

object CommitteeDto {
  val allNames = getAllNames

  def getOfName(name: String): List[Committee] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select distinct committee_id
          from vote_smart_candidate_committee
          where committee_name = '""" + name + """';"""

        Logger.info("ReportDto.getOfCandidate():" + query)

        SQL(query)().map {
          row =>
            Committee(row[Int]("committee_id"),
              name)
        }.toList
    }
  }

  private def getAllNames: List[String] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select distinct committee_name
          from vote_smart_candidate_committee
          where committee_name != ''
          order by committee_name;"""

        Logger.info("CommitteeDto.getAllNames():" + query)

        SQL(query)().map {
          row => row[String]("committee_name")
        }.toList
    }
  }
}
