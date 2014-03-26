package models

import play.api.libs.json.{Writes, JsValue, Json}


case class Committee(id: Int,
                     name: String)

object Committee {
  implicit val writes = new Writes[Committee] {
    def writes(committee: Committee): JsValue = {
      Json.obj(
        "id" -> committee.id,
        "name" -> committee.name
      )
    }
  }
}
