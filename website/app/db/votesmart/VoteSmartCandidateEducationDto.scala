package db.votesmart

import anorm._
import models.votesmart.VoteSmartCandidateEducation
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

// TODO: remove
object VoteSmartCandidateEducationDto {
  def create(voteSmartCandidateEducation: VoteSmartCandidateEducation): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                   insert into vote_smart_candidate_education(candidate_id, education)
          values(""" + voteSmartCandidateEducation.candidateId + """, '""" +
          DbUtil.backslashQuotes(voteSmartCandidateEducation.education) + """');"""

        Logger.debug("VoteSmartCandidateEducationDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}