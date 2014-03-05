package db.votesmart

import anorm._
import models.votesmart.VoteSmartCandidateOrgMembership
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

// TODO: remove
object VoteSmartCandidateOrgMembershipDto {
  def create(voteSmartCandidateOrgMembership: VoteSmartCandidateOrgMembership): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                   insert into vote_smart_candidate_org_membership(candidate_id, org_membership)
          values(""" + voteSmartCandidateOrgMembership.candidateId + """, '""" +
          DbUtil.backslashQuotes(voteSmartCandidateOrgMembership.orgMembership) + """');"""

        Logger.debug("VoteSmartCandidateOrgMembershipDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}