package services

import play.api.libs.ws.WS
import play.api.Logger
import play.api.libs.json.{JsObject, JsValue}
import models.votesmart.{VoteSmartLeadingOfficial, VoteSmartLeadershipPosition}
import db.votesmart.{VoteSmartLeadingOfficialDto, VoteSmartLeadershipPositionDto}
import scala.concurrent.ExecutionContext.Implicits.global
import db.UsStateDto
import play.api.Play.current

object VoteSmartLeadershipService {
  var isOfficialsWithLeadershipPositionWebServiceCallRunning = false
  var isOfficialsWithLeadershipPositionInStateWebServiceCallRunning = false

  def fetchLeaderships() {
    if (!VoteSmartService.isRunning) {
      VoteSmartService.isRunning = true

      WS.url("http://api.votesmart.org/Leadership.getPositions")
        .withQueryString("key" -> VoteSmartService.voteSmartApiKey)
        .withQueryString("o" -> "JSON")
        .get()
        .map {
        response =>
          try {
            processJsonFromVoteSmartLeadershipPositions(response.json)
          }
          catch {
            case e: Exception => Logger.error(ExceptionUtil.getExceptionString(e))
          }
      }
    }
  }

  private def processJsonFromVoteSmartLeadershipPositions(json: JsValue) {
    val voteSmartLeadershipPositionJsonList = (json \ "leadership" \ "position").as[List[JsObject]]

    for (voteSmartLeadershipPositionJson <- voteSmartLeadershipPositionJsonList) {
      val leadershipId = (voteSmartLeadershipPositionJson \ "leadershipId").as[String].toInt

      storeVoteSmartLeadershipPosition(voteSmartLeadershipPositionJson, leadershipId)

      while (isOfficialsWithLeadershipPositionWebServiceCallRunning) {
        //Pause for 100 ms
        Thread.sleep(100)
      }

      isOfficialsWithLeadershipPositionWebServiceCallRunning = true

      for (usState <- UsStateDto.all) {
        while (isOfficialsWithLeadershipPositionInStateWebServiceCallRunning) {
          //Pause for 100 ms
          Thread.sleep(100)
        }

        fetchOfficialsWithLeadershipPositionInState(leadershipId, usState.id)
      }

      // We wait for the insertion of the last state to be finished
      while (isOfficialsWithLeadershipPositionInStateWebServiceCallRunning) {
        //Pause for 100 ms
        Thread.sleep(100)
      }

      Logger.info("Processed leadership position " + leadershipId)

      isOfficialsWithLeadershipPositionWebServiceCallRunning = false
    }

    Logger.info("Processed " + voteSmartLeadershipPositionJsonList.length + " VoteSmart leadership positions")

    while (isOfficialsWithLeadershipPositionWebServiceCallRunning || isOfficialsWithLeadershipPositionInStateWebServiceCallRunning) {
      Thread.sleep(100)
    }

    VoteSmartService.isRunning = false
  }

  private def storeVoteSmartLeadershipPosition(voteSmartLeadershipPositionJson: JsObject, leadershipId: Int) {
    val name = (voteSmartLeadershipPositionJson \ "name").as[String]
    val officeId = (voteSmartLeadershipPositionJson \ "officeId").as[String].toInt
    val officeName = (voteSmartLeadershipPositionJson \ "officeName").as[String]

    VoteSmartLeadershipPositionDto.create(
      VoteSmartLeadershipPosition(leadershipId = leadershipId,
        positionName = name,
        officeId = officeId,
        officeName = officeName)
    )
  }

  private def fetchOfficialsWithLeadershipPositionInState(leadershipId: Int, usStateId: String) {
    isOfficialsWithLeadershipPositionInStateWebServiceCallRunning = true

    WS.url("http://api.votesmart.org/Leadership.getOfficials")
      .withQueryString("key" -> VoteSmartService.voteSmartApiKey)
      .withQueryString("o" -> "JSON")
      .withQueryString("leadershipId" -> leadershipId.toString)
      .withQueryString("stateId" -> usStateId)
      .get()
      .map {
      response =>
        try {
          processJsonFromVoteSmartLeaders(response.json, leadershipId, usStateId)
        }
        catch {
          case e: Exception => Logger.error(ExceptionUtil.getExceptionString(e))
        }
    }
  }

  private def processJsonFromVoteSmartLeaders(json: JsValue, leadershipId: Int, usStateId: String) {
    val voteSmartLeadersJsValue = json \ "leaders" \ "leader"

    val voteSmartLeaderJsonList = voteSmartLeadersJsValue.asOpt[List[JsObject]] match {
      case Some(jsonList) => jsonList
      case None => voteSmartLeadersJsValue.asOpt[JsObject] match {
        case Some(jsObject) => List(jsObject)
        case None => List()
      }
    }

    for (voteSmartLeaderJson <- voteSmartLeaderJsonList) {
      storeVoteSmartLeader(voteSmartLeaderJson, leadershipId, usStateId)
    }

    isOfficialsWithLeadershipPositionInStateWebServiceCallRunning = false
  }

  private def storeVoteSmartLeader(voteSmartLeaderJson: JsObject, leadershipId: Int, usStateId: String) {
    val candidateid = (voteSmartLeaderJson \ "candidateId").as[String].toInt
    val position = (voteSmartLeaderJson \ "position").as[String]

    VoteSmartLeadingOfficialDto.create(
      VoteSmartLeadingOfficial(leadershipId = leadershipId,
        candidateId = candidateid,
        positionName = position)
    )
  }
}
