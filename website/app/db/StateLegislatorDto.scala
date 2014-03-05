package db

import anorm._
import play.api.db.DB
import play.api.Logger
import models.StateLegislator
import play.api.Play.current

object StateLegislatorDto {
  def getOfId(id: Int): Option[StateLegislator] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select first_name, last_name, title, political_parties, us_state_id, district
          from state_legislator
          where id = """ + id + """;"""

        Logger.info("StateLegislatorDto.getOfId(" + id + "):" + query)

        SQL(query).apply().headOption match {
          case Some(sqlRow) =>
            var politicalParties: List[String] = List()

            sqlRow[Option[String]]("political_parties") match {
              case Some(commaSeparatedPoliticalParties) =>
                val politicalPartiesSplit = commaSeparatedPoliticalParties.split(",")
                for (untrimmedPoliticalParty <- politicalPartiesSplit) {
                  politicalParties = politicalParties :+ untrimmedPoliticalParty.trim()
                }
              case None =>
            }

            Some(
              new StateLegislator(
                id,
                sqlRow[String]("first_name"),
                sqlRow[String]("last_name"),
                sqlRow[String]("title"),
                politicalParties,
                sqlRow[String]("us_state_id"),
                sqlRow[String]("district")
              )
            )
          case None => None
        }
    }
  }

  def getMatching(firstNameFilter: Option[String], lastNameFilter: Option[String], usStateId: Option[String]): List[StateLegislator] = {
    DB.withConnection {
      implicit c =>

        val firstNameFilterForQuery = firstNameFilter match {
          case Some(fnFilter) => DbUtil.backslashQuotes(fnFilter.replaceAll("\\*", "%"))
          case None => "%"
        }

        val lastNameFilterForQuery = lastNameFilter match {
          case Some(lnFilter) => DbUtil.backslashQuotes(lnFilter.replaceAll("\\*", "%"))
          case None => "%"
        }

        val usStateIdFilterForQuery = usStateId match {
          case Some(stateId) => DbUtil.backslashQuotes(stateId)
          case None => "%"
        }

        val query = """
          select distinct id, first_name, last_name, title, political_parties, us_state_id, district
          from state_legislator
          where lower(first_name) like '""" + firstNameFilterForQuery + """'
            and lower(last_name) like '""" + lastNameFilterForQuery + """'
            and us_state_id like '""" + usStateIdFilterForQuery + """'
          order by last_name;"""

        Logger.info("StateLegislatorDto.getMatching():" + query)

        var result: List[StateLegislator] = List()

        for (row <- SQL(query)()) {
          var politicalParties: List[String] = List()

          row[Option[String]]("political_parties") match {
            case Some(commaSeparatedPoliticalParties) =>
              val politicalPartiesSplit = commaSeparatedPoliticalParties.split(",")
              for (untrimmedPoliticalParty <- politicalPartiesSplit) {
                politicalParties = politicalParties :+ untrimmedPoliticalParty.trim()
              }
            case None =>
          }

          result = result :+ new StateLegislator(
            row[Int]("id"),
            row[String]("first_name"),
            row[String]("last_name"),
            row[String]("title"),
            politicalParties,
            row[String]("us_state_id"),
            row[String]("district")
          )
        }

        result
    }
  }
}
