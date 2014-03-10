package models

import play.api.libs.json.{Json, JsValue, Writes}

case class CandidateOffice(candidateId: Int,
                           officeType: String,
                           street: Option[String],
                           city: Option[String],
                           usStateId: Option[String],
                           zip: Option[String],
                           phoneNumber: Option[String])

object CandidateOffice {
  implicit val writes = new Writes[CandidateOffice] {
    def writes(candidateOffice: CandidateOffice): JsValue = {
      Json.obj(
        "candidateId" -> candidateOffice.candidateId,
        "officeType" -> candidateOffice.officeType,
        "street" -> candidateOffice.street,
        "city" -> candidateOffice.city,
        "usStateId" -> candidateOffice.usStateId,
        "zip" -> candidateOffice.zip,
        "phoneNumber" -> candidateOffice.phoneNumber
      )
    }
  }
}
