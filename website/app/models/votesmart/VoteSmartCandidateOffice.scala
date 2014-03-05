package models.votesmart

import play.api.libs.json.{Json, JsValue, Writes}

case class VoteSmartCandidateOffice(id: Option[Long] = None,
                                    candidateId: Int,
                                    officeType: String,
                                    officeTypeId: Int,
                                    street: Option[String],
                                    city: Option[String],
                                    state: Option[String],
                                    zip: Option[String],
                                    phone1: Option[String])

object VoteSmartCandidateOffice {
  implicit val writes = new Writes[VoteSmartCandidateOffice] {
    def writes(candidateOffice: VoteSmartCandidateOffice): JsValue = {
      Json.obj(
        "id" -> candidateOffice.id.get,
        "candidateId" -> candidateOffice.candidateId,
        "officeType" -> candidateOffice.officeType,
        "officeTypeId" -> candidateOffice.officeTypeId,
        "street" -> candidateOffice.street,
        "city" -> candidateOffice.city,
        "state" -> candidateOffice.state,
        "zip" -> candidateOffice.zip,
        "phone1" -> candidateOffice.phone1
      )
    }
  }
}
