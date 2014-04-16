package db.votesmart

import anorm._
import models.votesmart.VoteSmartCommittee
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

object VoteSmartCommitteeDto {
  def create(voteSmartCommittee: VoteSmartCommittee): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                   insert into temp_vote_smart_committee(committee_id, state_id, committee_name)
          values(""" + voteSmartCommittee.id + """, '""" +
          DbUtil.safetize(voteSmartCommittee.usState.id) + """', '""" +
          DbUtil.safetize(voteSmartCommittee.name) + """');"""

        Logger.debug("VoteSmartCommitteeDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}
