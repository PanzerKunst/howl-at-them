package models.votesmart

import play.api.libs.json.{Json, JsValue, Writes}

// TODO: remove
case class VoteSmartCandidateBio(candidateId: Int,
                                 photo: String)

object VoteSmartCandidateBio {
  implicit val writes = new Writes[VoteSmartCandidateBio] {
    def writes(candidateBio: VoteSmartCandidateBio): JsValue = {
      Json.obj(
        "candidateId" -> candidateBio.candidateId,
        "photo" -> candidateBio.photo
      )
    }
  }
}
