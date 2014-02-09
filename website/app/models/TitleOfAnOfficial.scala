package models

import play.api.libs.json.{Json, JsValue, Writes}

case class TitleOfAnOfficial(id: Long,
                   name: String)

object TitleOfAnOfficial {
  implicit val writes = new Writes[TitleOfAnOfficial] {
    def writes(titleOfAnOfficial: TitleOfAnOfficial): JsValue = {
      Json.obj(
        "id" -> titleOfAnOfficial.id,
        "name" -> titleOfAnOfficial.name
      )
    }
  }
}