package models.votesmart

import play.api.libs.json.{Json, JsValue, Writes}

// TODO: remove
case class VoteSmartCandidateOrgMembership(id: Option[Long],
                                            candidateId: Int,
                                            orgMembership: String)

object VoteSmartCandidateOrgMembership {
  implicit val writes = new Writes[VoteSmartCandidateOrgMembership] {
    def writes(candidateOrgMembership: VoteSmartCandidateOrgMembership): JsValue = {
      Json.obj(
        "id" -> candidateOrgMembership.id.get,
        "candidateId" -> candidateOrgMembership.candidateId,
        "orgMembership" -> candidateOrgMembership.orgMembership
      )
    }
  }
}
