package db.votesmart

import anorm._
import models.votesmart.VoteSmartCandidateFamily
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

// TODO: remove
object VoteSmartCandidateFamilyDto {
  def create(voteSmartCandidateFamily: VoteSmartCandidateFamily): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                   insert into vote_smart_candidate_family(candidate_id, family)
          values(""" + voteSmartCandidateFamily.candidateId + """, '""" +
          DbUtil.backslashQuotes(voteSmartCandidateFamily.family) + """');"""

        Logger.debug("VoteSmartCandidateFamilyDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}