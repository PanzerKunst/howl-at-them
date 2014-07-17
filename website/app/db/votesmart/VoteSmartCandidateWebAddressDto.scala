package db.votesmart

import anorm._
import models.votesmart.VoteSmartCandidateWebAddress
import db.DbUtil
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

object VoteSmartCandidateWebAddressDto {
  def create(voteSmartCandidateWebAddress: VoteSmartCandidateWebAddress): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                   insert into temp_vote_smart_candidate_web_address(candidate_id,
                   web_address_type_id,
                   web_address_type,
                   web_address)
          values(""" + voteSmartCandidateWebAddress.candidateId + """, """ +
          voteSmartCandidateWebAddress.webAddressTypeId + """, '""" +
          DbUtil.safetize(voteSmartCandidateWebAddress.webAddressType) + """', '""" +
          DbUtil.safetize(voteSmartCandidateWebAddress.webAddress) + """');"""

        Logger.debug("VoteSmartCandidateWebAddressDto.create():" + query)

        SQL(query).executeInsert()
    }
  }
}