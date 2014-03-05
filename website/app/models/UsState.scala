package models

import play.api.libs.json.{Json, JsValue, Writes}

case class UsState(id: String,
                   name: String)

object UsState {
  implicit val writes = new Writes[UsState] {
    def writes(usState: UsState): JsValue = {
      Json.obj(
        "id" -> usState.id,
        "name" -> usState.name
      )
    }
  }
}
