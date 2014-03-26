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

                                   _offices: List[CandidateOffice] = List(),
                                   _committees: List[Committee] = List(),

                                   _reports: List[Report] = List())
  extends StateLegislator(_id,
    _firstName,
    _lastName,
    _title,
    _politicalParties,
    _usState,
    _district,
    _leadershipPosition,
    _otherPhoneNumber,
    _isAPriorityTarget) {

  var offices: List[CandidateOffice] = _offices
  var committees: List[Committee] = _committees

  var reports: List[Report] = _reports
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

        "offices" -> detailedStateLegislator.offices,
        "committees" -> detailedStateLegislator.committees,

        "reports" -> detailedStateLegislator.reports
      )
    }
  }
}
