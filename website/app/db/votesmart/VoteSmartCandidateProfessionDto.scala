package db.votesmart

import anorm._
import models.votesmart.VoteSmartCandidateProfession
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

// TODO: remove
object VoteSmartCandidateProfessionDto {
  def create(voteSmartCandidateProfession: VoteSmartCandidateProfession): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                   insert into vote_smart_candidate_profession(candidate_id, profession)
          values(""" + voteSmartCandidateProfession.candidateId + """, '""" +
          DbUtil.backslashQuotes(voteSmartCandidateProfession.profession) + """');"""

        Logger.debug("VoteSmartCandidateProfessionDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}