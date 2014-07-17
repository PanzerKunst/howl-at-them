package models.frontend

import play.api.libs.json.{Json, JsValue, Writes}
import models._

case class DetailedStateLegislator(_id: Int,
                                   _firstName: String,
                                   _lastName: String,
                                   _title: String,
                                   _politicalParties: List[String] = List(),
                                   _usState: UsState,
                                   _district: String,
                                   _leadershipPosition: Option[LeadershipPosition] = None,
                                   _otherPhoneNumber: Option[String] = None,
                                   _isAPriorityTarget: Boolean = false,
                                   _isMissingUrgentReport: Boolean = false,
                                   _staffName: Option[String] = None,
                                   _staffNumber: Option[String] = None,
                                   _pointOfContact: Option[String] = None,

                                   _offices: List[CandidateOffice] = List(),
                                   _committees: List[CandidateCommittee] = List(),

                                   _reports: List[Report] = List(),

                                   _emailAddress: Option[String] = None,
                                   _websiteUrl: Option[String] = None,
                                   _facebookUrl: Option[String] = None,
                                   _twitterUrl: Option[String] = None)
  extends StateLegislator(_id,
    _firstName,
    _lastName,
    _title,
    _politicalParties,
    _usState,
    _district,
    _leadershipPosition,
    _otherPhoneNumber,
    _isAPriorityTarget,
    _isMissingUrgentReport,
    _staffName,
    _staffNumber,
    _pointOfContact) {

  var offices: List[CandidateOffice] = _offices
  var committees: List[CandidateCommittee] = _committees

  var reports: List[Report] = _reports

  var emailAddress: Option[String] = _emailAddress
  var websiteUrl: Option[String] = _websiteUrl
  var facebookUrl: Option[String] = _facebookUrl
  var twitterUrl: Option[String] = _twitterUrl

  def getTitleAbbr: String = {
    this.title.toLowerCase match {
      case "representative" => "<abbr title=\"" + this.title + "\">Rep.</abbr>"
      case "senator" => "<abbr title=\"" + this.title + "\">Sen.</abbr>"
      case "assembly member" => "<abbr title=\"" + this.title + "\">Asm.</abbr>"
      case _ => this.title
    }
  }

  def getPoliticalPartiesAsString: String = {
    this.politicalParties.mkString(", ")
  }

  def getPoliticalPartiesAbbr: String = {
    if (this.politicalParties.isEmpty) {
      ""
    } else {
      this.politicalParties.head.toLowerCase match {
        case "democratic" =>
          "<abbr title=\"" + this.politicalParties.mkString(", ") + "\">D</abbr>"
        case "republican" =>
          "<abbr title=\"" + this.politicalParties.mkString(", ") + "\">R</abbr>"
        case _ =>
          val abbr = this.politicalParties.map {
            politicalParty => politicalParty.substring(0, 1)
          }.mkString("")
          "<abbr title=\"" + this.politicalParties.mkString(", ") + "\">" + abbr + "</abbr>"
      }
    }
  }

  def getChamber: Chamber = {
    if (this.title.toLowerCase == "senator")
      Chamber.SENATE
    else
      Chamber.HOUSE
  }

  def getPhotoUrl: String = {
    "http://static.votesmart.org/canphoto/" + this.id + ".jpg"
  }
}

object DetailedStateLegislator {
  implicit val writes = new Writes[DetailedStateLegislator] {
    def writes(detailedStateLegislator: DetailedStateLegislator): JsValue = {
      Json.obj(
        "id" -> detailedStateLegislator.id,
        "firstName" -> detailedStateLegislator.firstName,
        "lastName" -> detailedStateLegislator.lastName,
        "title" -> detailedStateLegislator.title,
        "politicalParties" -> detailedStateLegislator.politicalParties,
        "usState" -> detailedStateLegislator.usState,
        "district" -> detailedStateLegislator.district,
        "leadershipPosition" -> detailedStateLegislator.leadershipPosition,
        "otherPhoneNumber" -> detailedStateLegislator.otherPhoneNumber,
        "isAPriorityTarget" -> detailedStateLegislator.isAPriorityTarget,
        "isMissingUrgentReport" -> detailedStateLegislator.isMissingUrgentReport,

        "offices" -> detailedStateLegislator.offices,
        "committees" -> detailedStateLegislator.committees,

        "reports" -> detailedStateLegislator.reports,

        "emailAddress" -> detailedStateLegislator.emailAddress,
        "websiteUrl" -> detailedStateLegislator.websiteUrl,
        "facebookUrl" -> detailedStateLegislator.facebookUrl,
        "twitterUrl" -> detailedStateLegislator.twitterUrl
      )
    }
  }
}
