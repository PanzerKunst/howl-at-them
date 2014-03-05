package services

import db.UsStateDto
import play.api.libs.ws.WS
import play.api.Logger
import play.api.libs.json.{JsObject, JsValue}
import models.votesmart.{VoteSmartCandidateBio, VoteSmartCandidateOffice, VoteSmartCandidateCommittee, VoteSmartCandidate}
import models.UsState
import db.votesmart.{VoteSmartCandidateBioDto, VoteSmartCandidateOfficeDto, VoteSmartCandidateCommitteeDto, VoteSmartCandidateDto}
import play.Play
import scala.concurrent.ExecutionContext.Implicits.global

object VoteSmartCandidateService {
  val officeTypeIdLegislativeDistrict = "L"
  val voteSmartApiKey = Play.application().configuration().getString("votesmart.apikey")

  var isCandidatesWebServiceCallRunning = false
  var isCandidateBioWebServiceCallRunning = false
  var isCandidateOfficesWebServiceCallRunning = false

  def fetchCandidates() {
    for (usState <- UsStateDto.getAll) {
      isCandidatesWebServiceCallRunning = true

      Logger.info("Calling candidates WS for " + usState.id)

      WS.url("http://api.votesmart.org/Officials.getStatewide")
        .withQueryString("key" -> voteSmartApiKey)
        .withQueryString("o" -> "JSON")
        .withQueryString("stateId" -> usState.id)
        .get()
        .map {
        response =>
          try {
            processJsonFromVoteSmartOfficialsInUsState(response.json, usState)
          }
          catch {
            case e: Exception =>
              Logger.error(e.getStackTraceString)
              System.exit(1)
          }
      }

      while (isCandidatesWebServiceCallRunning) {
        //Pause for 100 ms
        Thread.sleep(100)
      }
    }
  }

  private def processJsonFromVoteSmartOfficialsInUsState(json: JsValue, usState: UsState) {
    Logger.info("Got candidates WS response for " + usState.id)

    val voteSmartCandidateJsonList = (json \ "candidateList" \ "candidate").as[List[JsObject]]

    for (voteSmartCandidateJson <- voteSmartCandidateJsonList) {
      val officeTypeId = (voteSmartCandidateJson \ "officeTypeId").as[String]

      if (officeTypeId == officeTypeIdLegislativeDistrict) {
        val candidateId = (voteSmartCandidateJson \ "candidateId").as[String].toInt

        storeVoteSmartOfficial(voteSmartCandidateJson, officeTypeId, candidateId)

        while (isCandidateBioWebServiceCallRunning) {
          //Pause for 100 ms
          Thread.sleep(100)
        }

        fetchCandidateBio(candidateId)

        while (isCandidateOfficesWebServiceCallRunning) {
          //Pause for 100 ms
          Thread.sleep(100)
        }

        fetchCandidateOffices(candidateId)
      }
    }

    isCandidatesWebServiceCallRunning = false

    Logger.info("Processed " + voteSmartCandidateJsonList.length + " VoteSmart candidates in " + usState.id)
  }

  private def storeVoteSmartOfficial(voteSmartCandidateJson: JsObject, officeTypeId: String, candidateId: Int) {
    Logger.debug("Trying to process\n" + voteSmartCandidateJson)

    val firstName = (voteSmartCandidateJson \ "firstName").as[String]

    val nickNameJson = (voteSmartCandidateJson \ "nickName").as[String]
    val nickName = if (nickNameJson != "") Some(nickNameJson) else None

    val middleNameJson = (voteSmartCandidateJson \ "middleName").as[String]
    val middleName = if (middleNameJson != "") Some(middleNameJson) else None

    val preferredNameJson = (voteSmartCandidateJson \ "preferredName").as[String]
    val preferredName = if (preferredNameJson != "") Some(preferredNameJson) else None

    val lastName = (voteSmartCandidateJson \ "lastName").as[String]

    val suffixJson = (voteSmartCandidateJson \ "suffix").as[String]
    val suffix = if (suffixJson != "") Some(suffixJson) else None

    val title = (voteSmartCandidateJson \ "title").as[String]

    val ballotNameJson = (voteSmartCandidateJson \ "ballotName").as[String]
    val ballotName = if (ballotNameJson != "") Some(ballotNameJson) else None

    val electionPartiesJson = (voteSmartCandidateJson \ "electionParties").as[String]
    val electionParties = if (electionPartiesJson != "") Some(electionPartiesJson) else None

    val electionDistrictIdJson = (voteSmartCandidateJson \ "electionDistrictId").as[String]
    val electionDistrictId = if (electionDistrictIdJson != "") Some(electionDistrictIdJson.toInt) else None

    val electionDistrictNameJson = (voteSmartCandidateJson \ "electionDistrictName").as[String]
    val electionDistrictName = if (electionDistrictNameJson != "") Some(electionDistrictNameJson) else None

    val electionOfficeJson = (voteSmartCandidateJson \ "electionOffice").as[String]
    val electionOffice = if (electionOfficeJson != "") Some(electionOfficeJson) else None

    val electionOfficeIdJson = (voteSmartCandidateJson \ "electionOfficeId").as[String]
    val electionOfficeId = if (electionOfficeIdJson != "") Some(electionOfficeIdJson.toInt) else None

    val electionStateIdJson = (voteSmartCandidateJson \ "electionStateId").as[String]
    val electionStateId = if (electionStateIdJson != "") Some(electionStateIdJson) else None

    val electionOfficeTypeIdJson = (voteSmartCandidateJson \ "electionOfficeTypeId").as[String]
    val electionOfficeTypeId = if (electionOfficeTypeIdJson != "") Some(electionOfficeTypeIdJson) else None

    val electionYearJson = (voteSmartCandidateJson \ "electionYear").as[String]
    val electionYear = if (electionYearJson != "") Some(electionYearJson.toInt) else None

    val officePartiesJson = (voteSmartCandidateJson \ "officeParties").as[String]
    val officeParties = if (officePartiesJson != "") Some(officePartiesJson) else None

    val officeDistrictIdJson = (voteSmartCandidateJson \ "officeDistrictId").as[String]
    val officeDistrictId = if (officeDistrictIdJson != "") Some(officeDistrictIdJson.toInt) else None

    val officeDistrictNameJson = (voteSmartCandidateJson \ "officeDistrictName").as[String]
    val officeDistrictName = if (officeDistrictNameJson != "") Some(officeDistrictNameJson) else None

    val officeStateId = (voteSmartCandidateJson \ "officeStateId").as[String]

    val officeId = (voteSmartCandidateJson \ "officeId").as[String].toInt

    val officeName = (voteSmartCandidateJson \ "officeName").as[String]

    Logger.debug("Trying to store candidate")

    VoteSmartCandidateDto.create(
      new VoteSmartCandidate(candidateId,
        firstName,
        nickName,
        middleName,
        preferredName,
        lastName,
        suffix,
        title,
        ballotName,
        electionParties,
        electionDistrictId,
        electionDistrictName,
        electionOffice,
        electionOfficeId,
        electionStateId,
        electionOfficeTypeId,
        electionYear,
        officeParties,
        officeDistrictId,
        officeDistrictName,
        officeStateId,
        officeId,
        officeName,
        officeTypeId)
    )
  }

  private def fetchCandidateBio(candidateId: Int) {
    isCandidateBioWebServiceCallRunning = true

    WS.url("http://api.votesmart.org/CandidateBio.getBio")
      .withQueryString("key" -> voteSmartApiKey)
      .withQueryString("o" -> "JSON")
      .withQueryString("candidateId" -> candidateId.toString)
      .get()
      .map {
      response =>
        try {
          processJsonFromVoteSmartCandidateBio(response.json, candidateId)
        }
        catch {
          case e: Exception =>
            Logger.error(e.getStackTraceString)
            System.exit(1)
        }
    }
  }

  private def fetchCandidateOffices(candidateId: Int) {
    isCandidateOfficesWebServiceCallRunning = true

    WS.url("http://api.votesmart.org/Address.getOffice")
      .withQueryString("key" -> voteSmartApiKey)
      .withQueryString("o" -> "JSON")
      .withQueryString("candidateId" -> candidateId.toString)
      .get()
      .map {
      response =>
        try {
          processJsonFromVoteSmartCandidateOffices(response.json, candidateId)
        }
        catch {
          case e: Exception =>
            Logger.error(e.getStackTraceString)
            System.exit(1)
        }
    }
  }

  private def processJsonFromVoteSmartCandidateBio(json: JsValue, candidateId: Int) {
    val voteSmartBioOfficeJsValue = (json \ "bio" \ "office")

    val voteSmartBioOfficeJsonList = voteSmartBioOfficeJsValue.asOpt[List[JsObject]] match {
      case Some(jsonList) => jsonList
      case None => voteSmartBioOfficeJsValue.asOpt[JsObject] match {
        case Some(jsObject) => List(jsObject)
        case None => List()
      }
    }

    for (voteSmartBioOfficeJson <- voteSmartBioOfficeJsonList) {
      val voteSmartBioCommitteesJsValue = (voteSmartBioOfficeJson \ "committee")

      val voteSmartBioCommitteesJsonList = voteSmartBioCommitteesJsValue.asOpt[List[JsObject]] match {
        case Some(jsonList) => jsonList
        case None => voteSmartBioCommitteesJsValue.asOpt[JsObject] match {
          case Some(jsObject) => List(jsObject)
          case None => List()
        }
      }

      processCommitteesJsonFromVoteSmartCandidatesBio(voteSmartBioCommitteesJsonList, candidateId)
    }

    isCandidateBioWebServiceCallRunning = false
  }

  private def processJsonFromVoteSmartCandidateOffices(json: JsValue, candidateId: Int) {
    (json \ "error").asOpt[JsObject] match {
      case None =>
        val voteSmartOfficeJsValue = (json \ "address" \ "office")

        val voteSmartOfficeJsonList = voteSmartOfficeJsValue.asOpt[List[JsObject]] match {
          case Some(jsonList) => jsonList
          case None => List(voteSmartOfficeJsValue.as[JsObject])
        }

        Logger.debug("Trying to process address/office\n" + voteSmartOfficeJsonList)

        for (voteSmartOfficeJson <- voteSmartOfficeJsonList) {
          storeVoteSmartCandidateOffice(voteSmartOfficeJson, candidateId)
        }
      case Some(error) =>
    }

    isCandidateOfficesWebServiceCallRunning = false
  }

  private def processCommitteesJsonFromVoteSmartCandidatesBio(voteSmartBioCommitteesJsonList: List[JsObject], candidateId: Int) {
    for (voteSmartBioCommitteesJson <- voteSmartBioCommitteesJsonList) {
      storeVoteSmartCommittee(voteSmartBioCommitteesJson, candidateId)
    }
  }

  private def storeVoteSmartCommittee(voteSmartBioCommitteesJson: JsObject, candidateId: Int) {
    val committeeId = (voteSmartBioCommitteesJson \ "committeeId").as[String].toInt
    val committeeName = (voteSmartBioCommitteesJson \ "committeeName").as[String]

    VoteSmartCandidateCommitteeDto.create(
      VoteSmartCandidateCommittee(candidateId = candidateId,
        committeeId = committeeId,
        committeeName = committeeName)
    )
  }

  private def storeVoteSmartCandidateOffice(voteSmartOfficeJson: JsObject, candidateId: Int) {
    val address = (voteSmartOfficeJson \ "address").as[JsObject]

    val officeType = (address \ "type").as[String]

    val officeTypeId = (address \ "typeId").as[String].toInt

    val streetJson = (address \ "street").as[String]
    val street = if (streetJson != "") Some(streetJson) else None

    val cityJson = (address \ "city").as[String]
    val city = if (cityJson != "") Some(cityJson) else None

    val stateJson = (address \ "state").as[String]
    val state = if (stateJson != "" && stateJson != "NA") Some(stateJson) else None

    val zipJson = (address \ "zip").as[String]
    val zip = if (zipJson != "") Some(zipJson) else None

    val phone1Json = (voteSmartOfficeJson \ "phone" \ "phone1").as[String]
    val phone1 = if (phone1Json != "") Some(phone1Json) else None

    VoteSmartCandidateOfficeDto.create(
      VoteSmartCandidateOffice(candidateId = candidateId,
        officeType = officeType,
        officeTypeId = officeTypeId,
        street = street,
        city = city,
        state = state,
        zip = zip,
        phone1 = phone1)
    )
  }
}
