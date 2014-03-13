package models.frontend

import play.api.libs.json.{Json, JsValue, Writes}
import models._

class DetailedStateLegislator(_id: Int,
                              _firstName: String,
                              _lastName: String,
                              _title: String,
                              _politicalParties: List[String] = List(),
                              _usState: UsState,
                              _district: String,
                              _leadershipPosition: Option[String] = None,

                              _offices: List[CandidateOffice] = List(),
                              _committees: List[CandidateCommittee] = List(),

                              _isAPriorityTarget: Boolean = false,
                              _reports: List[Report] = List())
  extends StateLegislator(_id,
    _firstName,
    _lastName,
    _title,
    _politicalParties,
    _usState,
    _district,
    _leadershipPosition) {

  var offices: List[CandidateOffice] = _offices
  var committees: List[CandidateCommittee] = _committees

  var isAPriorityTarget: Boolean = _isAPriorityTarget
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

        "offices" -> detailedStateLegislator.offices,
        "committees" -> detailedStateLegislator.committees,

        "isAPriorityTarget" -> detailedStateLegislator.isAPriorityTarget,
        "reports" -> detailedStateLegislator.reports
      )
    }
  }
}
