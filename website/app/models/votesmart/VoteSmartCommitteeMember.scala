package models.votesmart


case class VoteSmartCommitteeMember(id: Option[Long] = None,
                                    committeeId: Int,
                                    candidateId: Int,
                                    position: String)
