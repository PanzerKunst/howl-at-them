package db.votesmart

import anorm._
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import models.votesmart.VoteSmartCandidatePoliticalCareer
import play.api.Play.current

// TODO: remove
object VoteSmartCandidatePoliticalCareerDto {
  def create(voteSmartCandidatePoliticalCareer: VoteSmartCandidatePoliticalCareer): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                   insert into vote_smart_candidate_political_career(candidate_id, political_career)
          values(""" + voteSmartCandidatePoliticalCareer.candidateId + """, '""" +
          DbUtil.backslashQuotes(voteSmartCandidatePoliticalCareer.politicalCareer) + """');"""

        Logger.debug("VoteSmartCandidatePoliticalCareerDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}