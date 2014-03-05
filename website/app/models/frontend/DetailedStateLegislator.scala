package models.frontend

import play.api.libs.json.{Json, JsValue, Writes}
import models.StateLegislator
import models.votesmart.{VoteSmartCandidateOffice, VoteSmartCandidateCommittee}

class DetailedStateLegislator(_id: Int,
                              _firstName: String,
                              _lastName: String,
                              _title: String,
                              _politicalParties: List[String] = List(),
                              _usStateId: String,
                              _district: String,

                              _leadershipTitle: Option[String] = None,
                              _committees: List[VoteSmartCandidateCommittee] = List(),
                              _offices: List[VoteSmartCandidateOffice] = List(),
                              _photoUrl: Option[String] = None,

                              _moneyInPoliticsIsAProblem: Option[Boolean] = None,
                              _supportsAmendmentToFixIt: Option[Boolean] = None,
                              _opposesCitizensUnited: Option[Boolean] = None,
                              _previousVoteForConvention: Option[Boolean] = None,
                              _supportLevel: Option[String] = None,
                              _isAPriorityTarget: Boolean)
  extends StateLegislator(_id,
    _firstName,
    _lastName,
    _title,
    _politicalParties,
    _usStateId,
    _district) {

  var leadershipTitle: Option[String] = _leadershipTitle
  var committees: List[VoteSmartCandidateCommittee] = _committees
  var offices: List[VoteSmartCandidateOffice] = _offices
  var photoUrl: Option[String] = _photoUrl

  var moneyInPoliticsIsAProblem: Option[Boolean] = _moneyInPoliticsIsAProblem
  var supportsAmendmentToFixIt: Option[Boolean] = _supportsAmendmentToFixIt
  var opposesCitizensUnited: Option[Boolean] = _opposesCitizensUnited
  var previousVoteForConvention: Option[Boolean] = _previousVoteForConvention
  var supportLevel: Option[String] = _supportLevel
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
        "usStateId" -> detailedStateLegislator.usStateId,
        "district" -> detailedStateLegislator.district,

        "leadershipTitle" -> detailedStateLegislator.leadershipTitle,
        "committees" -> detailedStateLegislator.committees,
        "offices" -> detailedStateLegislator.offices,
        "photoUrl" -> detailedStateLegislator.photoUrl,

        "moneyInPoliticsIsAProblem" -> detailedStateLegislator.moneyInPoliticsIsAProblem,
        "supportsAmendmentToFixIt" -> detailedStateLegislator.supportsAmendmentToFixIt,
        "opposesCitizensUnited" -> detailedStateLegislator.opposesCitizensUnited,
        "previousVoteForConvention" -> detailedStateLegislator.previousVoteForConvention,
        "supportLevel" -> detailedStateLegislator.supportLevel,
        "isAPriorityTarget" -> detailedStateLegislator.isAPriorityTarget
      )
    }
  }
}
