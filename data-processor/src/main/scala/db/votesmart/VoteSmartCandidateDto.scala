package db.votesmart

import anorm._
import models.votesmart.VoteSmartCandidate
import db.DbUtil
import scalikejdbc.ConnectionPool
import com.typesafe.scalalogging.slf4j.Logging

object VoteSmartCandidateDto extends Logging {
  def create(voteSmartCandidate: VoteSmartCandidate): Option[Long] = {
    implicit val c = ConnectionPool.borrow()

    var nickNameForQuery = "NULL"
    if (voteSmartCandidate.nickName.isDefined && voteSmartCandidate.nickName.get != "")
      nickNameForQuery = "'" + DbUtil.backslashQuotes(voteSmartCandidate.nickName.get) + "'"

    var middleNameForQuery = "NULL"
    if (voteSmartCandidate.middleName.isDefined && voteSmartCandidate.middleName.get != "")
      middleNameForQuery = "'" + DbUtil.backslashQuotes(voteSmartCandidate.middleName.get) + "'"

    var preferredNameForQuery = "NULL"
    if (voteSmartCandidate.preferredName.isDefined && voteSmartCandidate.preferredName.get != "")
      preferredNameForQuery = "'" + DbUtil.backslashQuotes(voteSmartCandidate.preferredName.get) + "'"

    var suffixForQuery = "NULL"
    if (voteSmartCandidate.suffix.isDefined && voteSmartCandidate.suffix.get != "")
      suffixForQuery = "'" + DbUtil.backslashQuotes(voteSmartCandidate.suffix.get) + "'"

    var ballotNameForQuery = "NULL"
    if (voteSmartCandidate.ballotName.isDefined && voteSmartCandidate.ballotName.get != "")
      ballotNameForQuery = "'" + DbUtil.backslashQuotes(voteSmartCandidate.ballotName.get) + "'"

    var electionPartiesForQuery = "NULL"
    if (voteSmartCandidate.electionParties.isDefined && voteSmartCandidate.electionParties.get != "")
      electionPartiesForQuery = "'" + DbUtil.backslashQuotes(voteSmartCandidate.electionParties.get) + "'"

    var electionDistrictNameForQuery = "NULL"
    if (voteSmartCandidate.electionDistrictName.isDefined && voteSmartCandidate.electionDistrictName.get != "")
      electionDistrictNameForQuery = "'" + DbUtil.backslashQuotes(voteSmartCandidate.electionDistrictName.get) + "'"

    var electionOfficeForQuery = "NULL"
    if (voteSmartCandidate.electionOffice.isDefined && voteSmartCandidate.electionOffice.get != "")
      electionOfficeForQuery = "'" + DbUtil.backslashQuotes(voteSmartCandidate.electionOffice.get) + "'"

    var electionStateIdForQuery = "NULL"
    if (voteSmartCandidate.electionStateId.isDefined && voteSmartCandidate.electionStateId.get != "")
      electionStateIdForQuery = "'" + DbUtil.backslashQuotes(voteSmartCandidate.electionStateId.get) + "'"

    var electionOfficeTypeIdForQuery = "NULL"
    if (voteSmartCandidate.electionOfficeTypeId.isDefined && voteSmartCandidate.electionOfficeTypeId.get != "")
      electionOfficeTypeIdForQuery = "'" + DbUtil.backslashQuotes(voteSmartCandidate.electionOfficeTypeId.get) + "'"

    var officePartiesForQuery = "NULL"
    if (voteSmartCandidate.officeParties.isDefined && voteSmartCandidate.officeParties.get != "")
      officePartiesForQuery = "'" + DbUtil.backslashQuotes(voteSmartCandidate.officeParties.get) + "'"

    var officeDistrictNameForQuery = "NULL"
    if (voteSmartCandidate.officeDistrictName.isDefined && voteSmartCandidate.officeDistrictName.get != "")
      officeDistrictNameForQuery = "'" + DbUtil.backslashQuotes(voteSmartCandidate.officeDistrictName.get) + "'"

    val query = """
                   insert into vote_smart_candidates(candidate_id,
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
      DbUtil.backslashQuotes(voteSmartCandidate.firstName) + """', """ +
      nickNameForQuery + """, """ +
      middleNameForQuery + """, """ +
      preferredNameForQuery + """, '""" +
      DbUtil.backslashQuotes(voteSmartCandidate.lastName) + """', """ +
      suffixForQuery + """, '""" +
      DbUtil.backslashQuotes(voteSmartCandidate.title) + """', """ +
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
      voteSmartCandidate.officeStateId + """', """ +
      voteSmartCandidate.officeId + """, '""" +
      voteSmartCandidate.officeName + """', '""" +
      voteSmartCandidate.officeTypeId + """');"""

    logger.debug("VoteSmartCandidateDto.create():" + query)

    try {
      SQL(query).executeInsert()
    }
    finally {
      c.close()
    }
  }
}
