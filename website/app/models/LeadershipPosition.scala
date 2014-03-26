package models

import play.api.libs.json.{Writes, JsValue, Json}


case class LeadershipPosition(id: Int,
                              name: String)

object LeadershipPosition {
  implicit val writes = new Writes[LeadershipPosition] {
    def writes(leadershipPosition: LeadershipPosition): JsValue = {
      Json.obj(
        "id" -> leadershipPosition.id,
        "name" -> leadershipPosition.name
      )
    }
  }
}
