package models

import play.api.libs.json.{Writes, JsValue, Json}


case class Committee(committeeId: Int,
                     usState: UsState,
                     name: String)

object Committee {
  implicit val writes = new Writes[Committee] {
    def writes(committee: Committee): JsValue = {
      Json.obj(
        "committeeId" -> committee.committeeId,
        "usState" -> committee.usState,
        "name" -> committee.name
      )
    }
  }
}
