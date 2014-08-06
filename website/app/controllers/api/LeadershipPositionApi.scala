package controllers.api

import play.api.mvc.{Action, Controller}
import db.{LeadershipPositionDto, CommitteeDto, UsStateDto}
import play.api.libs.json.Json
import models.Committee
import util.control.Breaks._
import scala.Some

object LeadershipPositionApi extends Controller {
  def search = Action { request =>
      if (request.queryString.contains("usStateId")) {
        UsStateDto.getOfId(request.queryString.get("usStateId").get.head) match {
          case Some(usState) => Ok(Json.toJson(LeadershipPositionDto.getInState(usState.id)))
          case None => NoContent
        }
      } else
        Forbidden
  }
}
