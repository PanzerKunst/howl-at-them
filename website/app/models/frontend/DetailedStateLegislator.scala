package models.frontend

import play.api.libs.json.{Json, JsValue, Writes}
import models.{StateLegislator, UsState, CandidateCommittee, CandidateOffice}

class DetailedStateLegislator(_id: Int,
                              _firstName: String,
                              _lastName: String,
                              _title: String,
                              _politicalParties: List[String] = List(),
                              _usState: UsState,
                              _district: String,

                              _offices: List[CandidateOffice] = List(),
                              _committees: List[CandidateCommittee] = List(),
                              _leadershipTitle: Option[String] = None,

                              _isAPriorityTarget: Boolean = false)
  extends StateLegislator(_id,
    _firstName,
    _lastName,
    _title,
    _politicalParties,
    _usState,
    _district) {

  var offices: List[CandidateOffice] = _offices
  var committees: List[CandidateCommittee] = _committees
  var leadershipTitle: Option[String] = _leadershipTitle

  var isAPriorityTarget: Boolean = _isAPriorityTarget
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

        "offices" -> detailedStateLegislator.offices,
        "committees" -> detailedStateLegislator.committees,
        "leadershipTitle" -> detailedStateLegislator.leadershipTitle,

        "isAPriorityTarget" -> detailedStateLegislator.isAPriorityTarget
      )
    }
  }
}
