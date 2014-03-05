package models.votesmart

/* This is not a case class because of an arbitrary limit of the number of attributes
@see http://stackoverflow.com/questions/4152223/why-are-scala-functions-limited-to-22-parameters
 */
class VoteSmartCandidate {
  var candidateId: Int = _
  var firstName: String = _
  var nickName: Option[String] = None
  var middleName: Option[String] = None
  var preferredName: Option[String] = None
  var lastName: String = _
  var suffix: Option[String] = None
  var title: String = _
  var ballotName: Option[String] = None
  var electionParties: Option[String] = None
  var electionDistrictId: Option[Int] = None
  var electionDistrictName: Option[String] = None
  var electionOffice: Option[String] = None
  var electionOfficeId: Option[Int] = None
  var electionStateId: Option[String] = None
  var electionOfficeTypeId: Option[String] = None
  var electionYear: Option[Int] = None
  var officeParties: Option[String] = None
  var officeDistrictId: Option[Int] = None
  var officeDistrictName: Option[String] = None
  var officeStateId: String = _
  var officeId: Int = _
  var officeName: String = _
  var officeTypeId: String = _

  def this(candidateId: Int,
           firstName: String,
           nickName: Option[String],
           middleName: Option[String],
           preferredName: Option[String],
           lastName: String,
           suffix: Option[String],
           title: String,
           ballotName: Option[String],
           electionParties: Option[String],
           electionDistrictId: Option[Int],
           electionDistrictName: Option[String],
           electionOffice: Option[String],
           electionOfficeId: Option[Int],
           electionStateId: Option[String],
           electionOfficeTypeId: Option[String],
           electionYear: Option[Int],
           officeParties: Option[String],
           officeDistrictId: Option[Int],
           officeDistrictName: Option[String],
           officeStateId: String,
           officeId: Int,
           officeName: String,
           officeTypeId: String) = {
    this()

    this.candidateId = candidateId
    this.firstName = firstName
    this.nickName = nickName
    this.middleName = middleName
    this.preferredName = preferredName
    this.lastName = lastName
    this.suffix = suffix
    this.title = title
    this.ballotName = ballotName
    this.electionParties = electionParties
    this.electionDistrictId = electionDistrictId
    this.electionDistrictName = electionDistrictName
    this.electionOffice = electionOffice
    this.electionOfficeId = electionOfficeId
    this.electionStateId = electionStateId
    this.electionOfficeTypeId = electionOfficeTypeId
    this.electionYear = electionYear
    this.officeParties = officeParties
    this.officeDistrictId = officeDistrictId
    this.officeDistrictName = officeDistrictName
    this.officeStateId = officeStateId
    this.officeId = officeId
    this.officeName = officeName
    this.officeTypeId = officeTypeId
  }
}
