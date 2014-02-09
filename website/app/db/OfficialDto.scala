package db

import anorm._
import models.Official
import play.api.db.DB
import play.api.Logger
import java.util.Date
import play.api.Play.current


object OfficialDto {
  def create(official: Official): Option[Long] = {
    DB.withConnection {
      implicit c =>

        var phoneNumberForQuery = "NULL"
        if (official.phoneNumber.isDefined && official.phoneNumber.get != "")
          phoneNumberForQuery = "'" + DbUtil.backslashQuotes(official.phoneNumber.get) + "'"

        val query = """
                       insert into official(first_name, last_name, title_id, political_party_id, us_state_id, district, phone_number, ideology_ranking, creation_timestamp)
        values('""" + DbUtil.backslashQuotes(official.firstName) + """', '""" +
          DbUtil.backslashQuotes(official.lastName) + """', """ +
          official.titleId + """, """ +
          official.politicalPartyId + """, """ +
          official.usStateId + """, """ +
          official.district + """, """ +
          phoneNumberForQuery + """, """ +
          official.ideologyRanking.getOrElse("NULL") + """, """ +
          (new Date().getTime / 1000) + """);"""

        Logger.info("OfficialDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}
