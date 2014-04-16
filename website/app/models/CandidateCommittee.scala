package models

import play.api.libs.json.{Json, JsValue, Writes}


case class CandidateCommittee(id: Long,
                              committee: Committee,
                              position: String)

object CandidateCommittee {
  implicit val writes = new Writes[CandidateCommittee] {
    def writes(candidateCommittee: CandidateCommittee): JsValue = {
      Json.obj(
        "id" -> candidateCommittee.id,
        "committee" -> candidateCommittee.committee,
        "position" -> candidateCommittee.position
      )
    }
  }
}
