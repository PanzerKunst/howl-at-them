package models

import play.api.libs.json.{Json, JsValue, Writes}
import db.UsStateDto

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

  def getLinkOnIndexPage(id: String): String = {
    val usStateName = UsStateDto.getOfId(id).get.name

    val linkContent = if (id == "AL" || id == "CA" || id == "DE" || id == "FL" || id == "GA" || id == "HI" || id == "ID" || id == "KS"
      || id == "LA" || id == "ME" || id == "NE" || id == "OH" || id == "PA" || id == "RI" || id == "SC" || id == "TN"
      || id == "UT" || id == "VT" || id == "WA") {
      "<span>" + usStateName.head + "</span>" + usStateName.substring(1)
    }
    else {
      usStateName
    }

    "<a href=\"/state-legislators?usStateId=" + id + "\">" + linkContent + "</a>"
  }
}
