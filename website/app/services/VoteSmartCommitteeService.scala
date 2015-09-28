package services

import play.api.libs.ws.WS
import play.api.Logger
import play.api.libs.json.{JsObject, JsValue}
import models.votesmart.{VoteSmartCommitteeMember, VoteSmartCommittee}
import db.votesmart.{VoteSmartCommitteeMembershipDto, VoteSmartCommitteeDto}
import scala.concurrent.ExecutionContext.Implicits.global
import db.UsStateDto
import models.UsState
import play.api.Play.current

object VoteSmartCommitteeService {
  var isCommitteesWebServiceCallRunning = false
  var isCommitteeMembersWebServiceCallRunning = false

  def fetchCommittees() {
    if (!VoteSmartService.isRunning) {
      VoteSmartService.isRunning = true

      for (usState <- UsStateDto.all) {
        isCommitteesWebServiceCallRunning = true

        Logger.info("Calling committees WS for " + usState.id)

        WS.url("http://api.votesmart.org/Committee.getCommitteesByTypeState")
          .withQueryString("key" -> VoteSmartService.voteSmartApiKey)
          .withQueryString("o" -> "JSON")
          .withQueryString("stateId" -> usState.id)
          .get()
          .map {
          response =>
            try {
              processJsonFromVoteSmartCommittesOfUsState(response.json, usState)
            }
            catch {
              case e: Exception => Logger.error(ExceptionUtil.getExceptionString(e))
            }
        }

        while (isCommitteesWebServiceCallRunning) {
          //Pause for 100 ms
          Thread.sleep(100)
        }
      }

      VoteSmartService.isRunning = false
    }
  }

  private def processJsonFromVoteSmartCommittesOfUsState(json: JsValue, usState: UsState) {
    Logger.info("Got committees WS response for " + usState.id)

    (json \ "error").asOpt[JsObject] match {
      case None =>
        val voteSmartCommitteeJsonList = (json \ "committees" \ "committee").as[List[JsObject]]

        for (voteSmartCommitteeJson <- voteSmartCommitteeJsonList) {
          val committeeId = (voteSmartCommitteeJson \ "committeeId").as[String].toInt

          storeVoteSmartCommittee(voteSmartCommitteeJson, usState, committeeId)

          while (isCommitteeMembersWebServiceCallRunning) {
            //Pause for 100 ms
            Thread.sleep(100)
          }

          fetchCommitteeMembers(committeeId)
        }

        Logger.info("Processed " + voteSmartCommitteeJsonList.length + " VoteSmart committees in " + usState.id)
      case Some(error) =>
    }

    isCommitteesWebServiceCallRunning = false
  }

  private def storeVoteSmartCommittee(voteSmartCommitteeJson: JsObject, usState: UsState, committeeId: Int) {
    Logger.debug("Trying to process\n" + voteSmartCommitteeJson)

    val name = (voteSmartCommitteeJson \ "name").as[String]

    Logger.debug("Trying to store committee")

    VoteSmartCommitteeDto.create(
      new VoteSmartCommittee(committeeId,
        usState,
        name)
    )
  }

  private def fetchCommitteeMembers(committeeId: Int) {
    isCommitteeMembersWebServiceCallRunning = true

    WS.url("http://api.votesmart.org/Committee.getCommitteeMembers")
      .withQueryString("key" -> VoteSmartService.voteSmartApiKey)
      .withQueryString("o" -> "JSON")
      .withQueryString("committeeId" -> committeeId.toString)
      .get()
      .map {
      response =>
        try {
          processJsonFromVoteSmartCommitteeMembers(response.json, committeeId)
        }
        catch {
          case e: Exception => Logger.error(ExceptionUtil.getExceptionString(e))
        }
    }
  }

  private def processJsonFromVoteSmartCommitteeMembers(json: JsValue, committeeId: Int) {
    (json \ "error").asOpt[JsObject] match {
      case None =>
        val voteSmartCommitteeMembersJsValue = json \ "committeeMembers" \ "member"

        val voteSmartCommitteeMemberJsonList = voteSmartCommitteeMembersJsValue.asOpt[List[JsObject]] match {
          case Some(jsonList) => jsonList
          case None => List(voteSmartCommitteeMembersJsValue.as[JsObject])
        }

        Logger.debug("Trying to process members\n" + voteSmartCommitteeMemberJsonList)

        for (voteSmartCommitteeMemberJson <- voteSmartCommitteeMemberJsonList) {
          storeVoteSmartCommitteeMember(voteSmartCommitteeMemberJson, committeeId)
        }
      case Some(error) =>
    }

    isCommitteeMembersWebServiceCallRunning = false
  }

  private def storeVoteSmartCommitteeMember(voteSmartOfficeJson: JsObject, committeeId: Int) {
    val candidateId = (voteSmartOfficeJson \ "candidateId").as[String].toInt

    val position = (voteSmartOfficeJson \ "position").as[String]

    VoteSmartCommitteeMembershipDto.create(
      VoteSmartCommitteeMember(committeeId = committeeId,
        candidateId = candidateId,
        position = position)
    )
  }
}
