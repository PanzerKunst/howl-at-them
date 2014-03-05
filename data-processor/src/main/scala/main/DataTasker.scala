package main

import java.util.TimerTask
import play.api.libs.ws.WS
import models.votesmart.VoteSmartCandidate
import play.api.libs.json.{JsValue, JsObject}
import com.typesafe.config.ConfigFactory
import concurrent.ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory
import db.votesmart.VoteSmartCandidateDto
import db.UsStateDto
import models.UsState

object DataTasker extends TimerTask {
  // Because multiple-inheritence is not allowed
  val logger = LoggerFactory.getLogger(DataTasker.getClass)

  val officeTypeIdLegislativeDistrict = "L"

  var isRunning = false
  var isWebServiceCallRunning = false

  // TODO: remove
  def testRun() {
    val trickyState = new UsState("NY", "New York")

    WS.url("http://api.votesmart.org/Officials.getStatewide")
      .withQueryString("key" -> ConfigFactory.load().getString("votesmart.apikey"))
      .withQueryString("o" -> "JSON")
      .withQueryString("stateId" -> trickyState.id)
      .get()
      .map {
      response =>
        try {
          processJsonFromVoteSmartOfficialsInAUsState(response.json, trickyState)
        }
        catch {
          case e: Exception =>
            logger.error(e.getStackTraceString)
            System.exit(1)  // Because exceptions happening in Futures don't exit
        }
    }
  }

  def run() {
    if (!isRunning) {
      isRunning = true

      for (usState <- UsStateDto.getAll) {
        isWebServiceCallRunning = true

        logger.info("Calling WS for " + usState.id)

        WS.url("http://api.votesmart.org/Officials.getStatewide")
          .withQueryString("key" -> ConfigFactory.load().getString("votesmart.apikey"))
          .withQueryString("o" -> "JSON")
          .withQueryString("stateId" -> usState.id)
          .get()
          .map {
          response =>
            try {
              processJsonFromVoteSmartOfficialsInAUsState(response.json, usState)
            }
            catch {
              case e: Exception =>
                logger.error(e.getStackTraceString)
                System.exit(1)
            }
        }

        while (isWebServiceCallRunning) {
          //Pause for 500 ms
          Thread.sleep(500)
        }
      }

      isRunning = false
    }
  }

  private def processJsonFromVoteSmartOfficialsInAUsState(json: JsValue, usState: UsState) {
    logger.info("Got WS response from " + usState.id)

    val voteSmartCandidateJsonList = (json \ "candidateList" \ "candidate").as[List[JsObject]]

    for (voteSmartCandidateJson <- voteSmartCandidateJsonList) {
      val officeTypeId = (voteSmartCandidateJson \ "officeTypeId").as[String]

      if (officeTypeId == officeTypeIdLegislativeDistrict) {
        storeVoteSmartOfficial(voteSmartCandidateJson, officeTypeId)
      }
    }

    isWebServiceCallRunning = false

    logger.info("Processed " + voteSmartCandidateJsonList.length + " VoteSmart candidates in " + usState.id)
  }

  private def storeVoteSmartOfficial(voteSmartCandidateJson: JsObject, officeTypeId: String) {
    logger.debug("Trying to process\n" + voteSmartCandidateJson)

    val candidateId = (voteSmartCandidateJson \ "candidateId").as[String].toInt

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

    logger.debug("Trying to store candidate")

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
