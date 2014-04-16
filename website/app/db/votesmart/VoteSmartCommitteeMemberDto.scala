package db.votesmart

import anorm._
import models.votesmart.VoteSmartCommitteeMember
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

object VoteSmartCommitteeMemberDto {
  def create(voteSmartCommitteeMember: VoteSmartCommitteeMember): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                   insert into temp_vote_smart_committee_member(committee_id, candidate_id, position)
          values(""" + voteSmartCommitteeMember.committeeId + """, """ +
          voteSmartCommitteeMember.candidateId + """, '""" +
          DbUtil.safetize(voteSmartCommitteeMember.position) + """');"""

        Logger.debug("VoteSmartCommitteeMemberDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}
