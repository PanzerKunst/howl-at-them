package models

import play.api.libs.json.{Json, JsValue, Writes}

case class UsState(id: Long,
                   name: String,
                   abbr: String)

object UsState {
  implicit val writes = new Writes[UsState] {
    def writes(usState: UsState): JsValue = {
      Json.obj(
        "id" -> usState.id,
        "name" -> usState.name,
        "abbr" -> usState.abbr
      )
    }
  }
}