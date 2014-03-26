package db

import play.api.db.DB
import play.api.Logger
import anorm._
import play.api.Play.current
import models.UsState
import util.control.Breaks._

object UsStateDto {
  val all = getAll

  def getOfId(id: String): Option[UsState] = {
    var result: Option[UsState] = None
    breakable {
      for (usState <- all) {
        if (usState.id == id) {
          result = Some(usState)
          break()
        }
      }
    }
    result
  }

  private def getAll: List[UsState] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select id, name
          from us_state
          order by id;"""

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
}
