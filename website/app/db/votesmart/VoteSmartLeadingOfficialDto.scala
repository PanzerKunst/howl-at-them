package db.votesmart

import anorm._
import models.votesmart.VoteSmartLeadingOfficial
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

object VoteSmartLeadingOfficialDto {
  def create(voteSmartLeadingOfficial: VoteSmartLeadingOfficial): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                   insert into temp_vote_smart_leading_official(leadership_id, candidate_id, position_name)
          values(""" + voteSmartLeadingOfficial.leadershipId + """, """ +
          voteSmartLeadingOfficial.candidateId + """, '""" +
          DbUtil.safetize(voteSmartLeadingOfficial.positionName) + """');"""

        Logger.debug("VoteSmartLeadingOfficialDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}
