package models

import play.api.libs.json.{Json, JsValue, Writes}

case class StateLegislatorTitle(id: Long,
                                name: String)

object StateLegislatorTitle {
  implicit val writes = new Writes[StateLegislatorTitle] {
    def writes(stateLegislatorTitle: StateLegislatorTitle): JsValue = {
      Json.obj(
        "id" -> stateLegislatorTitle.id,
        "name" -> stateLegislatorTitle.name
      )
    }
  }
}
