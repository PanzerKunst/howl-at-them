package controllers.api

import play.api.mvc.{Action, Controller}
import db.StateLegislatorDto
import play.api.libs.json.Json

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

      if (!firstNameFilter.isDefined && !lastNameFilter.isDefined && !usStateId.isDefined) {
        Forbidden
      } else {
        val matchingLegislators = StateLegislatorDto.getMatching(firstNameFilter, lastNameFilter, usStateId)
        Ok(Json.toJson(matchingLegislators))
      }
  }
}
