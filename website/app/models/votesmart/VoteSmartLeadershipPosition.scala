package models.votesmart


case class VoteSmartLeadershipPosition(id: Option[Long] = None,
                                       leadershipId: Int,
                                       positionName: String,
                                       officeId: Int,
                                       officeName: String)
