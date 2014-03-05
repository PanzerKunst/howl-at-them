package models.votesmart

import play.api.libs.json.{Json, JsValue, Writes}

// TODO: remove
case class VoteSmartCandidateCongMembership(id: Option[Long],
                                            candidateId: Int,
                                            congMembership: String)

object VoteSmartCandidateCongMembership {
  implicit val writes = new Writes[VoteSmartCandidateCongMembership] {
    def writes(candidateCongMembership: VoteSmartCandidateCongMembership): JsValue = {
      Json.obj(
        "id" -> candidateCongMembership.id.get,
        "candidateId" -> candidateCongMembership.candidateId,
        "congMembership" -> candidateCongMembership.congMembership
      )
    }
  }
}
