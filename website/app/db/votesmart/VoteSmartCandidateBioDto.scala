package db.votesmart

import anorm._
import models.votesmart.VoteSmartCandidateBio
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

// TODO: remove
object VoteSmartCandidateBioDto {
  def create(voteSmartCandidateBio: VoteSmartCandidateBio): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                   insert into temp_vote_smart_candidate_bio(candidate_id, photo)
          values(""" + voteSmartCandidateBio.candidateId + """, '""" +
          DbUtil.backslashQuotes(voteSmartCandidateBio.photo) + """');"""

        Logger.debug("VoteSmartCandidateBioDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}
