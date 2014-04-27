package controllers.api

import play.api.mvc.{Action, Controller}
import db.{CommitteeDto, UsStateDto}
import play.api.libs.json.Json
import models.Committee
import util.control.Breaks._
import scala.Some

object CommitteeApi extends Controller {
  def search = Action {
    implicit request =>

      if (request.queryString.contains("usStateId")) {
        UsStateDto.getOfId(request.queryString.get("usStateId").get.head) match {
          case Some(usState) =>
            val committeesInState = CommitteeDto.getInState(usState.id)
            val nameOfMatchingCommittees = committeeNamesWithoutDuplicates(committeesInState)
            Ok(Json.toJson(nameOfMatchingCommittees))
          case None =>
            NoContent
        }
      } else
        Forbidden
  }

  def committeeNamesWithoutDuplicates(committeesWithDuplicates: List[Committee]): List[String] = {
    var result: List[String] = List()

    for (committee <- committeesWithDuplicates) {
      var isAlreadyCounted = false

      breakable {
        for (committeeName <- result) {
          if (committee.name == committeeName) {
            isAlreadyCounted = true
            break()
          }
        }
      }

      if (!isAlreadyCounted) {
        result = result :+ committee.name
      }
    }

    result
  }
}
