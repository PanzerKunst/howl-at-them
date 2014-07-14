package models

import play.api.libs.json.{Json, JsValue, Writes}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.text.SimpleDateFormat
import java.util.Date

case class Report(_id: Option[Long] = None,
                  _candidateId: Int,
                  _authorName: String,
                  _contact: Option[String],
                  _isMoneyInPoliticsAProblem: Option[Boolean] = None,
                  _isSupportingAmendmentToFixIt: Option[Boolean] = None,
                  _isOpposingCitizensUnited: Option[Boolean] = None,
                  _hasPreviouslyVotedForConvention: Option[Boolean] = None,
                  _supportLevel: String,
                  _notes: Option[String],
                  _creationTimestamp: Option[Long] = None,
                  _isDeleted: Boolean = false) {

  @JsonDeserialize(contentAs = classOf[java.lang.Long])
  val id: Option[Long] = _id

  val candidateId: Int = _candidateId
  val authorName: String = _authorName
  val contact: Option[String] = _contact
  val isMoneyInPoliticsAProblem: Option[Boolean] = _isMoneyInPoliticsAProblem
  val isSupportingAmendmentToFixIt: Option[Boolean] = _isSupportingAmendmentToFixIt
  val isOpposingCitizensUnited: Option[Boolean] = _isOpposingCitizensUnited
  val hasPreviouslyVotedForConvention: Option[Boolean] = _hasPreviouslyVotedForConvention
  val supportLevel: String = _supportLevel
  val notes: Option[String] = _notes
  val creationTimestamp: Option[Long] = _creationTimestamp
  val isDeleted: Boolean = _isDeleted

  def getReadableCreationTimestamp: String = {
    new SimpleDateFormat("MM/dd/YYYY h:mm aa").format(new Date(creationTimestamp.get))
  }

  def getReadableContact: String = {
    contact match {
      case Some("MET_LEGISLATOR") => ContactWithLegislator.MET_LEGISLATOR.getString
      case Some("TALKED_TO_LEGISLATOR") => ContactWithLegislator.TALKED_TO_LEGISLATOR.getString
      case Some("CONTACT_WITH_STAFF") => ContactWithLegislator.CONTACT_WITH_STAFF.getString
      case Some("WAITING_FOR_CALLBACK") => ContactWithLegislator.WAITING_FOR_CALLBACK.getString
      case Some("LEFT_VOICEMAIL") => ContactWithLegislator.LEFT_VOICEMAIL.getString
      case Some("NO_CONTACT") => ContactWithLegislator.NO_CONTACT.getString
      case Some(otherString) => "No contact"
      case None => "No contact"
    }
  }

  def getSupportLevelSpan: String = {
    supportLevel match {
      case "PRIMARY_SPONSOR" => "<span class=\"support-level " + SupportLevel.PRIMARY_SPONSOR + "\">" + SupportLevel.PRIMARY_SPONSOR.getString + "</span>"
      case "CO_SPONSOR" => "<span class=\"support-level " + SupportLevel.CO_SPONSOR + "\">" + SupportLevel.CO_SPONSOR.getString + "</span>"
      case "SUPPORTIVE" => "<span class=\"support-level " + SupportLevel.SUPPORTIVE + "\">" + SupportLevel.SUPPORTIVE.getString + "</span>"
      case "NEEDS_CONVINCING" => "<span class=\"support-level " + SupportLevel.NEEDS_CONVINCING + "\">" + SupportLevel.NEEDS_CONVINCING.getString + "</span>"
      case "NOT_SUPPORTIVE" => "<span class=\"support-level " + SupportLevel.NOT_SUPPORTIVE + "\">" + SupportLevel.NOT_SUPPORTIVE.getString + "</span>"
      case "UNKNOWN" => "<span class=\"support-level " + SupportLevel.UNKNOWN + "\">" + SupportLevel.UNKNOWN.getString + "</span>"
    }
  }

  def getNotesForWeb: String = {
    notes match {
      case Some(n0tes) =>
        n0tes.replaceAll("\n", "<br />")

      case None =>
        ""
    }
  }
}

object Report {
  implicit val writes = new Writes[Report] {
    def writes(report: Report): JsValue = {
      Json.obj(
        "id" -> report.id.get,
        "candidateId" -> report.candidateId,
        "authorName" -> report.authorName,
        "contact" -> report.contact,
        "isMoneyInPoliticsAProblem" -> report.isMoneyInPoliticsAProblem,
        "isSupportingAmendmentToFixIt" -> report.isSupportingAmendmentToFixIt,
        "isOpposingCitizensUnited" -> report.isOpposingCitizensUnited,
        "hasPreviouslyVotedForConvention" -> report.hasPreviouslyVotedForConvention,
        "supportLevel" -> report.supportLevel,
        "notes" -> report.notes,
        "creationTimestamp" -> report.creationTimestamp,
        "isDeleted" -> report.isDeleted
      )
    }
  }

  def getLetterForYesNoAnswer(answer: Option[Boolean]): String = {
    answer match {
      case Some(bool) => if (bool) "Y" else "N"
      case None => "?"
    }
  }

  def getSpanForYesNoAnswer(answer: Option[Boolean]): String = {
    val cssClass = answer match {
      case Some(bool) => if (bool) "YES" else "NO"
      case None => "UNKNOWN"
    }

    "<span class=\"yes-no-answer " + cssClass + "\">" + getLetterForYesNoAnswer(answer) + "</span>"
  }
}
