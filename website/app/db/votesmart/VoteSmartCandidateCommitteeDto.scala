package db.votesmart

import anorm._
import models.votesmart.VoteSmartCandidateCommittee
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

object VoteSmartCandidateCommitteeDto {
  def create(voteSmartCandidateCommittee: VoteSmartCandidateCommittee): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                   insert into temp_vote_smart_candidate_committee(candidate_id, committee_id, committee_name)
          values(""" + voteSmartCandidateCommittee.candidateId + """, """ +
          voteSmartCandidateCommittee.committeeId + """, '""" +
          DbUtil.safetize(voteSmartCandidateCommittee.committeeName) + """');"""

        Logger.debug("VoteSmartCandidateCommitteeDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}