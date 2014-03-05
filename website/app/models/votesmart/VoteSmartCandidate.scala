package models.votesmart

/* This is not a case class because of an arbitrary limit of the number of attributes
@see http://stackoverflow.com/questions/4152223/why-are-scala-functions-limited-to-22-parameters
 */
class VoteSmartCandidate(_candidateId: Int,
                         _firstName: String,
                         _nickName: Option[String] = None,
                         _middleName: Option[String] = None,
                         _preferredName: Option[String] = None,
                         _lastName: String,
                         _suffix: Option[String] = None,
                         _title: String,
                         _ballotName: Option[String] = None,
                         _electionParties: Option[String] = None,
                         _electionDistrictId: Option[Int] = None,
                         _electionDistrictName: Option[String] = None,
                         _electionOffice: Option[String] = None,
                         _electionOfficeId: Option[Int] = None,
                         _electionStateId: Option[String] = None,
                         _electionOfficeTypeId: Option[String] = None,
                         _electionYear: Option[Int] = None,
                         _officeParties: Option[String] = None,
                         _officeDistrictId: Option[Int] = None,
                         _officeDistrictName: Option[String] = None,
                         _officeStateId: String,
                         _officeId: Int,
                         _officeName: String,
                         _officeTypeId: String) {

  var candidateId: Int = _candidateId
  var firstName: String = _firstName
  var nickName: Option[String] = _nickName
  var middleName: Option[String] = _middleName
  var preferredName: Option[String] = _preferredName
  var lastName: String = _lastName
  var suffix: Option[String] = _suffix
  var title: String = _title
  var ballotName: Option[String] = _ballotName
  var electionParties: Option[String] = _electionParties
  var electionDistrictId: Option[Int] = _electionDistrictId
  var electionDistrictName: Option[String] = _electionDistrictName
  var electionOffice: Option[String] = _electionOffice
  var electionOfficeId: Option[Int] = _electionOfficeId
  var electionStateId: Option[String] = _electionStateId
  var electionOfficeTypeId: Option[String] = _electionOfficeTypeId
  var electionYear: Option[Int] = _electionYear
  var officeParties: Option[String] = _officeParties
  var officeDistrictId: Option[Int] = _officeDistrictId
  var officeDistrictName: Option[String] = _officeDistrictName
  var officeStateId: String = _officeStateId
  var officeId: Int = _officeId
  var officeName: String = _officeName
  var officeTypeId: String = _officeTypeId
}
