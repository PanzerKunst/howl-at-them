package db.votesmart

import anorm._
import models.votesmart.VoteSmartCandidateCongMembership
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

// TODO: remove
object VoteSmartCandidateCongMembershipDto {
  def create(voteSmartCandidateCongMembership: VoteSmartCandidateCongMembership): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                   insert into vote_smart_candidate_cong_membership(candidate_id, cong_membership)
          values(""" + voteSmartCandidateCongMembership.candidateId + """, '""" +
          DbUtil.backslashQuotes(voteSmartCandidateCongMembership.congMembership) + """');"""

        Logger.debug("VoteSmartCandidateCongMembershipDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}