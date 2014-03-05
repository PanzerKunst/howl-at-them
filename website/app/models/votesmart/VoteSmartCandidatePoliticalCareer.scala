package models.votesmart

import play.api.libs.json.{Json, JsValue, Writes}

// TODO: remove
case class VoteSmartCandidatePoliticalCareer(id: Option[Long],
                                             candidateId: Int,
                                             politicalCareer: String)

object VoteSmartCandidatePoliticalCareer {
  implicit val writes = new Writes[VoteSmartCandidatePoliticalCareer] {
    def writes(candidatePoliticalCareer: VoteSmartCandidatePoliticalCareer): JsValue = {
      Json.obj(
        "id" -> candidatePoliticalCareer.id.get,
        "candidateId" -> candidatePoliticalCareer.candidateId,
        "politicalCareer" -> candidatePoliticalCareer.politicalCareer
      )
    }
  }
}
