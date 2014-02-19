package db

import anorm._
import models.StateLegislator
import play.api.db.DB
import play.api.Logger
import java.util.Date
import play.api.Play.current


object StateLegislatorDto {
  def create(stateLegislator: StateLegislator): Option[Long] = {
    DB.withConnection {
      implicit c =>

        var phoneNumberForQuery = "NULL"
        if (stateLegislator.phoneNumber.isDefined && stateLegislator.phoneNumber.get != "")
          phoneNumberForQuery = "'" + DbUtil.backslashQuotes(stateLegislator.phoneNumber.get) + "'"

        val query = """
                       insert into state_legislators(first_name, last_name, title_id, political_party_id, us_state_id, district, phone_number, ideology_ranking, creation_timestamp)
        values('""" + DbUtil.backslashQuotes(stateLegislator.firstName) + """', '""" +
          DbUtil.backslashQuotes(stateLegislator.lastName) + """', """ +
          stateLegislator.titleId + """, """ +
          stateLegislator.politicalPartyId + """, """ +
          stateLegislator.usStateId + """, """ +
          stateLegislator.district + """, """ +
          phoneNumberForQuery + """, """ +
          stateLegislator.ideologyRanking.getOrElse("NULL") + """, """ +
          (new Date().getTime / 1000) + """);"""

        Logger.info("StateLegislatorDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}
