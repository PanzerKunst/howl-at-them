package db.votesmart

import anorm._
import models.votesmart.VoteSmartCandidateOffice
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

object VoteSmartCandidateOfficeDto {
  def create(voteSmartCandidateOffice: VoteSmartCandidateOffice): Option[Long] = {
    DB.withConnection {
      implicit c =>

        var streetForQuery = "NULL"
        if (voteSmartCandidateOffice.street.isDefined && voteSmartCandidateOffice.street.get != "")
          streetForQuery = "'" + DbUtil.safetize(voteSmartCandidateOffice.street.get) + "'"

        var cityForQuery = "NULL"
        if (voteSmartCandidateOffice.city.isDefined && voteSmartCandidateOffice.city.get != "")
          cityForQuery = "'" + DbUtil.safetize(voteSmartCandidateOffice.city.get) + "'"

        var stateForQuery = "NULL"
        if (voteSmartCandidateOffice.state.isDefined && voteSmartCandidateOffice.state.get != "")
          stateForQuery = "'" + DbUtil.safetize(voteSmartCandidateOffice.state.get) + "'"

        var zipForQuery = "NULL"
        if (voteSmartCandidateOffice.zip.isDefined && voteSmartCandidateOffice.zip.get != "")
          zipForQuery = "'" + DbUtil.safetize(voteSmartCandidateOffice.zip.get) + "'"

        var phone1ForQuery = "NULL"
        if (voteSmartCandidateOffice.phone1.isDefined && voteSmartCandidateOffice.phone1.get != "")
          phone1ForQuery = "'" + DbUtil.safetize(voteSmartCandidateOffice.phone1.get) + "'"

        val query = """
                   insert into temp_vote_smart_candidate_office(candidate_id,
                   office_type,
                   office_type_id,
                   street,
                   city,
                   state,
                   zip,
                   phone1)
          values(""" + voteSmartCandidateOffice.candidateId + """, '""" +
          DbUtil.safetize(voteSmartCandidateOffice.officeType) + """', """ +
          voteSmartCandidateOffice.officeTypeId + """, """ +
          streetForQuery + """, """ +
          cityForQuery + """, """ +
          stateForQuery + """, """ +
          zipForQuery + """, """ +
          phone1ForQuery + """);"""

        Logger.debug("VoteSmartCandidateOfficeDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}