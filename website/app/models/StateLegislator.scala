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
                      _isAPriorityTarget: Boolean = false) {

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

  def isUpperHouse: Boolean = {
    this.title.toLowerCase == "senator"
  }

  def isLowerHouse: Boolean = {
    this.title.toLowerCase == "representative" || this.title.toLowerCase == "assembly member"
  }

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

  def getChamber: String = {
    if (this.title.toLowerCase == "senator")
      "SD"
    else
      "HD"
  }

  def getPhotoUrl: String = {
    "http://static.votesmart.org/canphoto/" + this.id + ".jpg"
  }
}
