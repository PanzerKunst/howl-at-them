package db

import anorm._
import scalikejdbc.ConnectionPool
import models.UsState
import com.typesafe.scalalogging.slf4j.Logging

object UsStateDto extends Logging {
  def getAll: List[UsState] = {
    implicit val c = ConnectionPool.borrow()

    val query = """
          select id, name
          from us_states;"""

    logger.info("UsStateDto.getAll():" + query)

    try {
      SQL(query)().map(row =>
        new UsState(
          row[String]("id"),
          row[String]("name")
        )
      ).toList
    }
    finally {
      c.close()
    }
  }
}
