package models.votesmart

import play.api.libs.json.{Json, JsValue, Writes}

// TODO: remove
case class VoteSmartCandidateEducation(id: Option[Long],
                              candidateId: Int,
                              education: String)

object VoteSmartCandidateEducation {
  implicit val writes = new Writes[VoteSmartCandidateEducation] {
    def writes(candidateEducation: VoteSmartCandidateEducation): JsValue = {
      Json.obj(
        "id" -> candidateEducation.id.get,
        "candidateId" -> candidateEducation.candidateId,
        "education" -> candidateEducation.education
      )
    }
  }
}
