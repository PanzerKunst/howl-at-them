package db.votesmart

import anorm._
import models.votesmart.VoteSmartCandidate
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

object VoteSmartCandidateDto {
  def isCandidateOfIdAlreadyExisting(candidateId: Int): Boolean = {
    DB.withConnection {
      implicit c =>

        val query = """
          select 1
          from temp_vote_smart_candidate
          where candidate_id = """ + candidateId + """;"""

        Logger.debug("VoteSmartCandidateDto.isCandidateOfIdAlreadyExisting():" + query)

        SQL(query).apply().headOption match {
          case Some(row) => true
          case None => false
        }
    }
  }

  def create(voteSmartCandidate: VoteSmartCandidate): Option[Long] = {
    DB.withConnection {
      implicit c =>

        var nickNameForQuery = "NULL"
        if (voteSmartCandidate.nickName.isDefined && voteSmartCandidate.nickName.get != "")
          nickNameForQuery = "'" + DbUtil.safetize(voteSmartCandidate.nickName.get) + "'"

        var middleNameForQuery = "NULL"
        if (voteSmartCandidate.middleName.isDefined && voteSmartCandidate.middleName.get != "")
          middleNameForQuery = "'" + DbUtil.safetize(voteSmartCandidate.middleName.get) + "'"

        var preferredNameForQuery = "NULL"
        if (voteSmartCandidate.preferredName.isDefined && voteSmartCandidate.preferredName.get != "")
          preferredNameForQuery = "'" + DbUtil.safetize(voteSmartCandidate.preferredName.get) + "'"

        var suffixForQuery = "NULL"
        if (voteSmartCandidate.suffix.isDefined && voteSmartCandidate.suffix.get != "")
          suffixForQuery = "'" + DbUtil.safetize(voteSmartCandidate.suffix.get) + "'"

        var ballotNameForQuery = "NULL"
        if (voteSmartCandidate.ballotName.isDefined && voteSmartCandidate.ballotName.get != "")
          ballotNameForQuery = "'" + DbUtil.safetize(voteSmartCandidate.ballotName.get) + "'"

        var electionPartiesForQuery = "NULL"
        if (voteSmartCandidate.electionParties.isDefined && voteSmartCandidate.electionParties.get != "")
          electionPartiesForQuery = "'" + DbUtil.safetize(voteSmartCandidate.electionParties.get) + "'"

        var electionDistrictNameForQuery = "NULL"
        if (voteSmartCandidate.electionDistrictName.isDefined && voteSmartCandidate.electionDistrictName.get != "")
          electionDistrictNameForQuery = "'" + DbUtil.safetize(voteSmartCandidate.electionDistrictName.get) + "'"

        var electionOfficeForQuery = "NULL"
        if (voteSmartCandidate.electionOffice.isDefined && voteSmartCandidate.electionOffice.get != "")
          electionOfficeForQuery = "'" + DbUtil.safetize(voteSmartCandidate.electionOffice.get) + "'"

        var electionStateIdForQuery = "NULL"
        if (voteSmartCandidate.electionStateId.isDefined && voteSmartCandidate.electionStateId.get != "")
          electionStateIdForQuery = "'" + DbUtil.safetize(voteSmartCandidate.electionStateId.get) + "'"

        var electionOfficeTypeIdForQuery = "NULL"
        if (voteSmartCandidate.electionOfficeTypeId.isDefined && voteSmartCandidate.electionOfficeTypeId.get != "")
          electionOfficeTypeIdForQuery = "'" + DbUtil.safetize(voteSmartCandidate.electionOfficeTypeId.get) + "'"

        var officePartiesForQuery = "NULL"
        if (voteSmartCandidate.officeParties.isDefined && voteSmartCandidate.officeParties.get != "")
          officePartiesForQuery = "'" + DbUtil.safetize(voteSmartCandidate.officeParties.get) + "'"

        var officeDistrictNameForQuery = "NULL"
        if (voteSmartCandidate.officeDistrictName.isDefined && voteSmartCandidate.officeDistrictName.get != "")
          officeDistrictNameForQuery = "'" + DbUtil.safetize(voteSmartCandidate.officeDistrictName.get) + "'"

        val query = """
                   insert into temp_vote_smart_candidate(candidate_id,
                                                      first_name,
                                                      nick_name,
                                                      middle_name,
                                                      preferred_name,
                                                      last_name,
                                                      suffix,
                                                      title,
                                                      ballot_name,
                                                      election_parties,
                                                      election_district_id,
                                                      election_district_name,
                                                      election_office,
                                                      election_office_id,
                                                      election_state_id,
                                                      election_office_type_id,
                                                      election_year,
                                                      office_parties,
                                                      office_district_id,
                                                      office_district_name,
                                                      office_state_id,
                                                      office_id,
                                                      office_name,
                                                      office_type_id)
          values(""" + voteSmartCandidate.candidateId + """, '""" +
          DbUtil.safetize(voteSmartCandidate.firstName) + """', """ +
          nickNameForQuery + """, """ +
          middleNameForQuery + """, """ +
          preferredNameForQuery + """, '""" +
          DbUtil.safetize(voteSmartCandidate.lastName) + """', """ +
          suffixForQuery + """, '""" +
          DbUtil.safetize(voteSmartCandidate.title) + """', """ +
          ballotNameForQuery + """, """ +
          electionPartiesForQuery + """, """ +
          voteSmartCandidate.electionDistrictId.getOrElse("NULL") + """, """ +
          electionDistrictNameForQuery + """, """ +
          electionOfficeForQuery + """, """ +
          voteSmartCandidate.electionOfficeId.getOrElse("NULL") + """, """ +
          electionStateIdForQuery + """, """ +
          electionOfficeTypeIdForQuery + """, """ +
          voteSmartCandidate.electionYear.getOrElse("NULL") + """, """ +
          officePartiesForQuery + """, """ +
          voteSmartCandidate.officeDistrictId.getOrElse("NULL") + """, """ +
          officeDistrictNameForQuery + """, '""" +
          DbUtil.safetize(voteSmartCandidate.officeStateId) + """', """ +
          voteSmartCandidate.officeId + """, '""" +
          DbUtil.safetize(voteSmartCandidate.officeName) + """', '""" +
          DbUtil.safetize(voteSmartCandidate.officeTypeId) + """');"""

        Logger.debug("VoteSmartCandidateDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}