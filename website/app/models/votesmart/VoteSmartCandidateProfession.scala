package models.votesmart

import play.api.libs.json.{Json, JsValue, Writes}

// TODO: remove
case class VoteSmartCandidateProfession(id: Option[Long],
                              candidateId: Int,
                              profession: String)

object VoteSmartCandidateProfession {
  implicit val writes = new Writes[VoteSmartCandidateProfession] {
    def writes(candidateProfession: VoteSmartCandidateProfession): JsValue = {
      Json.obj(
        "id" -> candidateProfession.id.get,
        "candidateId" -> candidateProfession.candidateId,
        "profession" -> candidateProfession.profession
      )
    }
  }
}
