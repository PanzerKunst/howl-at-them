package controllers.api

import controllers.Application
import db.{CommitteeDto, StateLegislatorDto}
import models.frontend.DetailedStateLegislator
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.JsonUtil

object StateLegislatorApi extends Controller {
  def search = Action { request =>
    if (request.queryString.contains("usStateId")) {
      val usStateId = request.queryString.get("usStateId").get.head

      var newSession = request.session + ("selectedUsStateId" -> usStateId)

      val nbDaysSinceLastReport = if (request.queryString.contains("nbDaysSinceLastReport")) {
        val nbDays = request.queryString.get("nbDaysSinceLastReport").get.head
        newSession = newSession + ("selectedNbDaysSinceLastReport" -> nbDays)
        Some(nbDays.toInt)
      } else {
        newSession = newSession - "selectedNbDaysSinceLastReport"
        None
      }

      val chamberAbbrOrPriorityTarget = if (request.queryString.contains("chamberAbbrOrPriorityTarget")) {
        val chamberOrTarget = request.queryString.get("chamberAbbrOrPriorityTarget").get.head
        newSession = newSession + ("selectedChamberOrTargetSearchCriteria" -> chamberOrTarget)
        Some(chamberOrTarget)
      } else {
        newSession = newSession - "selectedChamberOrTargetSearchCriteria"
        None
      }

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

      val matchingLegislators = if (nbDaysSinceLastReport.isEmpty && chamberAbbrOrPriorityTarget.isEmpty && leadershipPositionId.isEmpty && committees.isEmpty) {
        StateLegislatorDto.getOfStateId(usStateId)
      } else {
        StateLegislatorDto.getMatching(usStateId, nbDaysSinceLastReport, chamberAbbrOrPriorityTarget, leadershipPositionId, committees)
      }

      Ok(Json.toJson(matchingLegislators))
        .withSession(newSession)
        .withHeaders(Application.doNotCachePage: _*)
    } else
      Forbidden
  }

  def update = Action(parse.json) { request =>
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
