package models.votesmart

import play.api.libs.json.{Json, JsValue, Writes}

// TODO: remove
case class VoteSmartCandidateFamily(id: Option[Long],
                              candidateId: Int,
                              family: String)

object VoteSmartCandidateFamily {
  implicit val writes = new Writes[VoteSmartCandidateFamily] {
    def writes(candidateFamily: VoteSmartCandidateFamily): JsValue = {
      Json.obj(
        "id" -> candidateFamily.id.get,
        "candidateId" -> candidateFamily.candidateId,
        "family" -> candidateFamily.family
      )
    }
  }
}
