package models

import play.api.libs.json.{Json, JsValue, Writes}

case class CandidateCommittee(candidateId: Int,
                              committeeId: Option[Int],
                              committeeName: Option[String])

object CandidateCommittee {
  implicit val writes = new Writes[CandidateCommittee] {
    def writes(candidateCommittee: CandidateCommittee): JsValue = {
      Json.obj(
        "candidateId" -> candidateCommittee.candidateId,
        "committeeId" -> candidateCommittee.committeeId,
        "committeeName" -> candidateCommittee.committeeName
      )
    }
  }
}
