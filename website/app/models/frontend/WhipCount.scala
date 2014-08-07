package models.frontend

import models.SupportLevel
import play.api.libs.json.{Json, JsValue, Writes}

case class WhipCount(supportLevel: SupportLevel,
                     count: Int,
                     percentage: Int)

object WhipCount {
  implicit val writes = new Writes[WhipCount] {
    def writes(whipCount: WhipCount): JsValue = {
      Json.obj(
        "supportLevel" -> Json.obj(
          "code" -> whipCount.supportLevel.name,
          "label" -> whipCount.supportLevel.getString
        ),
        "count" -> whipCount.count,
        "percentage" -> whipCount.percentage
      )
    }
  }
}
