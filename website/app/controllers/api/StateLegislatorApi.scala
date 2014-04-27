package controllers.api

import play.api.mvc.{Action, Controller}
import db.{CommitteeDto, StateLegislatorDto}
import play.api.libs.json.Json
import services.JsonUtil
import models.frontend.DetailedStateLegislator

object StateLegislatorApi extends Controller {
  def search = Action {
    implicit request =>

      if (request.queryString.contains("usStateId")) {
        val usStateId = request.queryString.get("usStateId").get.head

        var newSession = session + ("selectedUsStateId" -> usStateId)

        val leadershipPositionId = if (request.queryString.contains("leadershipPositionId")) {
          val id = request.queryString.get("leadershipPositionId").get.head
          newSession = newSession + ("selectedLeadershipPositionId" -> id)
          Some(id.toInt)
        } else {
          newSession = newSession - "selectedLeadershipPositionId"
          None
        }

        val committees = if (request.queryString.contains("committeeName")) {
          val committeeName = request.queryString.get("committeeName").get.head
          newSession = newSession + ("selectedCommitteeName" -> committeeName)
          CommitteeDto.getOfNameInState(committeeName, usStateId)
        } else {
          newSession = newSession - "selectedCommitteeName"
          List()
        }

        val isAPriorityTarget = if (request.queryString.contains("isAPriorityTarget") && request.queryString.get("isAPriorityTarget").get.head.toBoolean) {
          newSession = newSession + ("selectedIsAPriorityTarget" -> "true")
          true
        } else {
          newSession = newSession - "selectedIsAPriorityTarget"
          false
        }

        val matchingLegislators = StateLegislatorDto.getMatching(usStateId, leadershipPositionId, committees, isAPriorityTarget)

        Ok(Json.toJson(matchingLegislators)).withSession(newSession)
      } else
        Forbidden
  }

  def update = Action(parse.json) {
    implicit request =>

      val stateLegislator = JsonUtil.deserialize[DetailedStateLegislator](request.body.toString())

      StateLegislatorDto.getOfId(stateLegislator.id) match {
        case None =>
          BadRequest("No state legislator found for id '" + stateLegislator.id + "'")
        case Some(existingStateLegislator) =>
          if (StateLegislatorDto.doesStateLegislatorHaveExtraInfo(existingStateLegislator)) {
            StateLegislatorDto.updateExtras(stateLegislator)
          } else {
            StateLegislatorDto.insertExtras(stateLegislator)
          }

          Ok
      }
  }
}
