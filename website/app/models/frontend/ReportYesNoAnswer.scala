package models.frontend

import play.api.libs.json.{Json, JsValue, Writes}

class ReportYesNoAnswer(_value: Option[Boolean] = None) {
  var value: Option[Boolean] = _value

  override def toString: String = {
    value match {
      case Some(bool) => if (bool) "Y" else "N"
      case None => "?"
    }
  }
}

object ReportYesNoAnswer {
  implicit val writes = new Writes[ReportYesNoAnswer] {
    def writes(answer: ReportYesNoAnswer): JsValue = {
      Json.obj(
        "value" -> answer.value
      )
    }
  }
}
