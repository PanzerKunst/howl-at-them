package models


class StateLegislator(_id: Int,
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
                      _pointOfContact: Option[String] = None) {

  var id: Int = _id
  var firstName: String = _firstName
  var lastName: String = _lastName
  var title: String = _title
  var politicalParties: List[String] = _politicalParties
  var usState: UsState = _usState
  var district: String = _district
  var leadershipPosition: Option[LeadershipPosition] = _leadershipPosition
  var otherPhoneNumber: Option[String] = _otherPhoneNumber
  var isAPriorityTarget: Boolean = _isAPriorityTarget
  var isMissingUrgentReport: Boolean = _isMissingUrgentReport
  var staffName: Option[String] = _staffName
  var staffNumber: Option[String] = _staffNumber
  var pointOfContact: Option[String] = _pointOfContact
}
