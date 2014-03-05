package models

import play.api.libs.json.{Json, JsValue, Writes}


class StateLegislator(_id: Int,
                      _firstName: String,
                      _lastName: String,
                      _title: String,
                      _politicalParties: List[String] = List(),
                      _usStateId: String,
                      _district: String) {
  var id: Int = _id
  var firstName: String = _firstName
  var lastName: String = _lastName
  var title: String = _title
  var politicalParties: List[String] = _politicalParties
  var usStateId: String = _usStateId
  var district: String = _district

  def getTitleAbbr: String = {
    this.title.toLowerCase match {
      case "representative" => "<abbr title=\"" + this.title + "\">Rep.</abbr>"
      case "senator" => "<abbr title=\"" + this.title + "\">Sen.</abbr>"
      case "assembly member" => "<abbr title=\"" + this.title + "\">AM</abbr>"
      case _ => this.title
    }
  }

  def getPoliticalPartiesAsString: String = {
    this.politicalParties.mkString(", ")
  }

  def getPoliticalPartiesForTableCell: String = {
    this.politicalParties.head.toLowerCase match {
      case "democratic" =>
        "<abbr title=\"" + this.politicalParties.mkString(", ") + "\">D</abbr>"
      case "republican" =>
        "<abbr title=\"" + this.politicalParties.mkString(", ") + "\">R</abbr>"
      case _ =>
        val abbr = this.politicalParties.map(politicalParty => politicalParty.substring(0, 1)).mkString("")
        "<abbr title=\"" + this.politicalParties.mkString(", ") + "\">" + abbr + "</abbr>"
    }
  }
}

object StateLegislator {
  implicit val writes = new Writes[StateLegislator] {
    def writes(stateLegislator: StateLegislator): JsValue = {
      Json.obj(
        "id" -> stateLegislator.id,
        "firstName" -> stateLegislator.firstName,
        "lastName" -> stateLegislator.lastName,
        "title" -> stateLegislator.title,
        "politicalParties" -> stateLegislator.politicalParties,
        "usStateId" -> stateLegislator.usStateId,
        "district" -> stateLegislator.district
      )
    }
  }
}