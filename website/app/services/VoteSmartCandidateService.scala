package services

import db.UsStateDto
import db.votesmart.{VoteSmartCandidateDto, VoteSmartCandidateOfficeDto, VoteSmartCandidateWebAddressDto}
import models.UsState
import models.votesmart.{VoteSmartCandidate, VoteSmartCandidateOffice, VoteSmartCandidateWebAddress}
import play.api.Logger
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.ws.WS
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

object VoteSmartCandidateService {
  private var isCandidatesWebServiceCallRunning = false
  private var isCandidateOfficesWebServiceCallRunning = false
  private var isCandidateCampaignWebAddressesWebServiceCallRunning = false

  def fetchCandidates() {
    if (!VoteSmartService.isRunning) {
      VoteSmartService.isRunning = true

      for (usState <- UsStateDto.all) {
        isCandidatesWebServiceCallRunning = true

        Logger.info("Calling candidates WS for " + usState.id)

        WS.url("http://api.votesmart.org/Officials.getStatewide")
          .withQueryString("key" -> VoteSmartService.voteSmartApiKey)
          .withQueryString("o" -> "JSON")
          .withQueryString("stateId" -> usState.id)
          .get()
          .map {
          response =>
            try {
              processJsonFromVoteSmartOfficialsInUsState(response.json, usState)
            }
            catch {
              case e: Exception => Logger.error(e.getStackTraceString)
            }
        }

        while (isCandidatesWebServiceCallRunning) {
          //Pause for 100 ms
          Thread.sleep(100)
        }
      }

      VoteSmartService.isRunning = false
    }
  }

  private def processJsonFromVoteSmartOfficialsInUsState(json: JsValue, usState: UsState) {
    Logger.info("Got candidates WS response for " + usState.id)

    val voteSmartCandidateJsonList = (json \ "candidateList" \ "candidate").as[List[JsObject]]

    for (voteSmartCandidateJson <- voteSmartCandidateJsonList) {
      val officeTypeId = (voteSmartCandidateJson \ "officeTypeId").as[String]

      val candidateId = (voteSmartCandidateJson \ "candidateId").as[String].toInt

      storeVoteSmartOfficial(voteSmartCandidateJson, officeTypeId, candidateId)

      while (isCandidateOfficesWebServiceCallRunning) {
        //Pause for 100 ms
        Thread.sleep(100)
      }

      fetchCandidateOffices(candidateId)

      while (isCandidateCampaignWebAddressesWebServiceCallRunning) {
        //Pause for 100 ms
        Thread.sleep(100)
      }

      fetchCandidateCampaignWebAddresses(candidateId)
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
    val electionStateId = if (electionStateIdJson != "" && electionStateIdJson != "NA") Some(electionStateIdJson) else None

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

    if (!VoteSmartCandidateDto.isCandidateOfIdAlreadyExisting(candidateId)) {
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
  }

  private def fetchCandidateOffices(candidateId: Int) {
    isCandidateOfficesWebServiceCallRunning = true

    WS.url("http://api.votesmart.org/Address.getOffice")
      .withQueryString("key" -> VoteSmartService.voteSmartApiKey)
      .withQueryString("o" -> "JSON")
      .withQueryString("candidateId" -> candidateId.toString)
      .get()
      .map {
      response =>
        try {
          processJsonFromVoteSmartCandidateOffices(response.json, candidateId)
        }
        catch {
          case e: Exception => Logger.error(e.getStackTraceString)
        }
    }
  }

  private def processJsonFromVoteSmartCandidateOffices(json: JsValue, candidateId: Int) {
    (json \ "error").asOpt[JsObject] match {
      case None =>
        val voteSmartOfficesJsValue = (json \ "address" \ "office")

        val voteSmartOfficeJsonList = voteSmartOfficesJsValue.asOpt[List[JsObject]] match {
          case Some(jsonList) => jsonList
          case None => List(voteSmartOfficesJsValue.as[JsObject])
        }

        Logger.debug("Trying to process address/office\n" + voteSmartOfficeJsonList)

        for (voteSmartOfficeJson <- voteSmartOfficeJsonList) {
          storeVoteSmartCandidateOffice(voteSmartOfficeJson, candidateId)
        }
      case Some(error) =>
    }

    isCandidateOfficesWebServiceCallRunning = false
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

  private def fetchCandidateCampaignWebAddresses(candidateId: Int) {
    isCandidateCampaignWebAddressesWebServiceCallRunning = true

    WS.url("http://api.votesmart.org/Address.getCampaignWebAddress")
      .withQueryString("key" -> VoteSmartService.voteSmartApiKey)
      .withQueryString("o" -> "JSON")
      .withQueryString("candidateId" -> candidateId.toString)
      .get()
      .map {
      response =>
        try {
          processJsonFromVoteSmartCandidateWebAddresses(response.json, candidateId)
        }
        catch {
          case e: Exception => Logger.error(e.getStackTraceString)
        }
    }
  }

  private def processJsonFromVoteSmartCandidateWebAddresses(json: JsValue, candidateId: Int) {
    (json \ "error").asOpt[JsObject] match {
      case None =>
        val voteSmartWebAddressesJsValue = (json \ "webaddress" \ "address")

        val voteSmartWebAddressJsonList = voteSmartWebAddressesJsValue.asOpt[List[JsObject]] match {
          case Some(jsonList) => jsonList
          case None => List(voteSmartWebAddressesJsValue.as[JsObject])
        }

        Logger.debug("Trying to process webaddress/address\n" + voteSmartWebAddressJsonList)

        for (voteSmartWebAddressJson <- voteSmartWebAddressJsonList) {
          storeVoteSmartCandidateWebAddress(voteSmartWebAddressJson, candidateId)
        }
      case Some(error) =>
    }

    isCandidateCampaignWebAddressesWebServiceCallRunning = false
  }

  private def storeVoteSmartCandidateWebAddress(voteSmartWebAddressJson: JsObject, candidateId: Int) {
    val webAddressTypeId = (voteSmartWebAddressJson \ "webAddressTypeId").as[String].toInt

    val webAddressType = (voteSmartWebAddressJson \ "webAddressType").as[String]

    val webAddress = (voteSmartWebAddressJson \ "webAddress").as[String]

    VoteSmartCandidateWebAddressDto.create(
      VoteSmartCandidateWebAddress(candidateId = candidateId,
        webAddressTypeId = webAddressTypeId,
        webAddressType = webAddressType,
        webAddress = webAddress)
    )
  }
}
