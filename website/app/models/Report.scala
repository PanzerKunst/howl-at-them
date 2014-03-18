package models

import play.api.libs.json.{Json, JsValue, Writes}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.text.SimpleDateFormat
import java.util.Date

case class Report(_id: Option[Long] = None,
                  _candidateId: Int,
                  _authorName: String,
                  _contact: String,
                  _isMoneyInPoliticsAProblem: Option[Boolean] = None,
                  _isSupportingAmendmentToFixIt: Option[Boolean] = None,
                  _isOpposingCitizensUnited: Option[Boolean] = None,
                  _hasPreviouslyVotedForConvention: Option[Boolean] = None,
                  _supportLevel: Option[String] = None,
                  _notes: Option[String],
                  _creationTimestamp: Option[Long] = None,
                  _isDeleted: Boolean = false) {

  @JsonDeserialize(contentAs = classOf[java.lang.Long])
  val id: Option[Long] = _id

  val candidateId: Int = _candidateId
  val authorName: String = _authorName
  val contact: String = _contact
  val isMoneyInPoliticsAProblem: Option[Boolean] = _isMoneyInPoliticsAProblem
  val isSupportingAmendmentToFixIt: Option[Boolean] = _isSupportingAmendmentToFixIt
  val isOpposingCitizensUnited: Option[Boolean] = _isOpposingCitizensUnited
  val hasPreviouslyVotedForConvention: Option[Boolean] = _hasPreviouslyVotedForConvention
  val supportLevel: Option[String] = _supportLevel
  val notes: Option[String] = _notes
  val creationTimestamp: Option[Long] = _creationTimestamp
  val isDeleted: Boolean = _isDeleted

  def getReadableCreationTimestamp: String = {
    new SimpleDateFormat("MM/dd/YYYY h:mm aa").format(new Date(creationTimestamp.get))
  }

  def getReadableContact: String = {
    contact match {
      case "MET_LEGISLATOR" => ContactWithLegislator.MET_LEGISLATOR.getString
      case "TALKED_TO_LEGISLATOR" => ContactWithLegislator.TALKED_TO_LEGISLATOR.getString
      case "CONTACT_WITH_STAFF" => ContactWithLegislator.CONTACT_WITH_STAFF.getString
      case "WAITING_FOR_CALLBACK" => ContactWithLegislator.WAITING_FOR_CALLBACK.getString
      case "LEFT_VOICEMAIL" => ContactWithLegislator.LEFT_VOICEMAIL.getString
      case "NONE" => ContactWithLegislator.NONE.getString
    }
  }

  def getSupportLevelSpan: String = {
    supportLevel match {
      case Some("SUPPORTIVE") => "<span class=\"support-level " + SupportLevel.SUPPORTIVE + "\">" + SupportLevel.SUPPORTIVE.getString + "</span>"
      case Some("NEEDS_CONVINCING") => "<span class=\"support-level " + SupportLevel.NEEDS_CONVINCING + "\">" + SupportLevel.NEEDS_CONVINCING.getString + "</span>"
      case Some("NOT_SUPPORTIVE") => "<span class=\"support-level " + SupportLevel.NOT_SUPPORTIVE + "\">" + SupportLevel.NOT_SUPPORTIVE.getString + "</span>"
      case Some(otherString) => "<span class=\"support-level UNKNOWN\">Unknown</span>"
      case None => "<span class=\"support-level UNKNOWN\">Unknown</span>"
    }
  }

  def getNotesForWeb: String = {
    notes match {
      case Some(n0tes) => n0tes.replaceAll("\\\\n", "<br />")
      case None => ""
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
    "<span class=\"one-letter-cell\">" + getLetterForYesNoAnswer(answer) + "</span>"
  }
}
