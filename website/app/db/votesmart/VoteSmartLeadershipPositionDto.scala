package db.votesmart

import anorm._
import models.votesmart.VoteSmartLeadershipPosition
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

object VoteSmartLeadershipPositionDto {
  def create(voteSmartLeadershipPosition: VoteSmartLeadershipPosition): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                   insert into temp_vote_smart_leadership_position(leadership_id, position_name, office_id, office_name)
          values(""" + voteSmartLeadershipPosition.leadershipId + """, '""" +
          DbUtil.safetize(voteSmartLeadershipPosition.positionName) + """', """ +
          voteSmartLeadershipPosition.officeId + """, '""" +
          DbUtil.safetize(voteSmartLeadershipPosition.officeName) + """');"""

        Logger.debug("VoteSmartLeadershipPositionDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}
