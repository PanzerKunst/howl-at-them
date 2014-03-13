package models.votesmart

case class VoteSmartLeadingOfficial(id: Option[Long] = None,
                                    leadershipId: Int,
                                    candidateId: Int,
                                    positionName: String)
