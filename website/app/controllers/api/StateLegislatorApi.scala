package controllers.api

import play.api.mvc.{Action, Controller}
import db.{CommitteeDto, StateLegislatorDto}
import play.api.libs.json.Json
import services.JsonUtil
import models.frontend.DetailedStateLegislator

object StateLegislatorApi extends Controller {
  def search = Action {
    implicit request =>

      val firstNameFilter = if (request.queryString.contains("firstName"))
        Some(request.queryString.get("firstName").get.head)
      else
        None

      val lastNameFilter = if (request.queryString.contains("lastName"))
        Some(request.queryString.get("lastName").get.head)
      else
        None

      val usStateId = if (request.queryString.contains("usStateId"))
        Some(request.queryString.get("usStateId").get.head)
      else
        None

      val committees = if (request.queryString.contains("committeeName")) {
        val committeeName = request.queryString.get("committeeName").get.head
        CommitteeDto.getOfName(committeeName)
      } else
        List()

      val isAPriorityTarget = if (request.queryString.contains("isAPriorityTarget"))
        request.queryString.get("isAPriorityTarget").get.head.toBoolean
      else
        false

      if (!firstNameFilter.isDefined && !lastNameFilter.isDefined && !usStateId.isDefined && committees.isEmpty && !isAPriorityTarget) {
        Forbidden
      } else {
        val matchingLegislators = StateLegislatorDto.getMatching(firstNameFilter, lastNameFilter, usStateId, committees, isAPriorityTarget)

        usStateId match {
          case Some(id) =>
            Ok(Json.toJson(matchingLegislators)).withSession(
              session + ("selectedUsStateId" -> id)
            )
          case None =>
            Ok(Json.toJson(matchingLegislators))
        }
      }
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
