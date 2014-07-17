package models.votesmart

import play.api.libs.json.{Json, JsValue, Writes}

case class VoteSmartCandidateWebAddress(id: Option[Long] = None,
                                        candidateId: Int,
                                        webAddressTypeId: Int,
                                        webAddressType: String,
                                        webAddress: String)

object VoteSmartCandidateWebAddress {
  implicit val writes = new Writes[VoteSmartCandidateWebAddress] {
    def writes(candidateWebAddress: VoteSmartCandidateWebAddress): JsValue = {
      Json.obj(
        "id" -> candidateWebAddress.id.get,
        "candidateId" -> candidateWebAddress.candidateId,
        "webAddressTypeId" -> candidateWebAddress.webAddressTypeId,
        "webAddressType" -> candidateWebAddress.webAddressType,
        "webAddress" -> candidateWebAddress.webAddress
      )
    }
  }

  val emailTypeId = 1
  val websiteTypeId = 3
  val facebookTypeId = 5
  val twitterTypeId = 7
}
