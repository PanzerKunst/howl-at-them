package db

import play.api.db.DB
import play.api.Logger
import anorm._
import play.api.Play.current
import models.{UsState, Committee}

object CommitteeDto {
  val all = getAll

  def getOfState(stateId: String): List[Committee] = {
    var committeesOfState: List[Committee] = List()

    for (committee <- all) {
      if (committee.usState.id == stateId) {
        committeesOfState = committeesOfState :+ committee
      }
    }

    committeesOfState
  }

  def getOfNameInState(name: String, stateId: String): List[Committee] = {
    var committeesOfNameInState: List[Committee] = List()

    for (committee <- getOfState(stateId)) {
      if (committee.name == name) {
        committeesOfNameInState = committeesOfNameInState :+ committee
      }
    }

    committeesOfNameInState
  }

  private def getAll: List[Committee] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select distinct c.committee_id, c.us_state_id, c.committee_name,
            s.name
          from vote_smart_committee c
          inner join us_state s on s.id = c.us_state_id
          order by us_state_id, committee_name;"""

        Logger.info("CommitteeDto.getAll():" + query)

        SQL(query)().map {
          row => Committee(row[Int]("committee_id"),
            UsState(row[String]("us_state_id"), row[String]("name")),
            row[String]("committee_name")
          )
        }.toList
    }
  }
}
