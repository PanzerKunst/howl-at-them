package db

import play.api.db.DB
import play.api.Logger
import anorm._
import play.api.Play.current
import models.{LeadershipPosition, Committee}
import util.control.Breaks._

object LeadershipPositionDto {
  val all = getAll

  def getOfId(id: Int): Option[LeadershipPosition] = {
    var result: Option[LeadershipPosition] = None
    breakable {
      for (leadershipPosition <- all) {
        if (leadershipPosition.id == id) {
          result = Some(leadershipPosition)
          break()
        }
      }
    }
    result
  }

  private def getAll: List[LeadershipPosition] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select distinct leadership_id, position_name
          from vote_smart_leadership_position
          order by position_name;"""

        Logger.info("LeadershipPositionDto.getAll():" + query)

        SQL(query)().map {
          row =>
            LeadershipPosition(
              row[Int]("leadership_id"),
              row[String]("position_name")
            )
        }.toList
    }
  }
}
