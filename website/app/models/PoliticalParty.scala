package models

import play.api.libs.json.{Json, JsValue, Writes}

case class PoliticalParty(id: Long,
                   name: String)

object PoliticalParty {
  implicit val writes = new Writes[PoliticalParty] {
    def writes(politicalParty: PoliticalParty): JsValue = {
      Json.obj(
        "id" -> politicalParty.id,
        "name" -> politicalParty.name
      )
    }
  }
}