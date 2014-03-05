package models.votesmart

import play.api.libs.json.{Json, JsValue, Writes}

case class VoteSmartCandidateCommittee(id: Option[Long] = None,
                                       candidateId: Int,
                                       committeeId: Int,
                                       committeeName: String)

object VoteSmartCandidateCommittee {
  implicit val writes = new Writes[VoteSmartCandidateCommittee] {
    def writes(candidateCommittee: VoteSmartCandidateCommittee): JsValue = {
      Json.obj(
        "id" -> candidateCommittee.id.get,
        "candidateId" -> candidateCommittee.candidateId,
        "committeeId" -> candidateCommittee.committeeId,
        "committeeName" -> candidateCommittee.committeeName
      )
    }
  }
}
