package controllers.api

import play.api.mvc.{Action, Controller}
import models.Report
import services.JsonUtil
import db.ReportDto

object ReportApi extends Controller {
  def create = Action(parse.json) {
    implicit request =>

      val reportToCreate = JsonUtil.deserialize[Report](request.body.toString())
      ReportDto.create(reportToCreate) match {
        case Some(id) => Ok(id.toString)
        case None => InternalServerError("Creation of a report did not return an ID!")
      }
  }

  def delete(id: Long) = Action {
    implicit request =>

      ReportDto.getOfId(id) match {
        case None =>
          BadRequest("No report found for id '" + id + "'")
        case Some(report) =>
          ReportDto.delete(report)
          Ok
      }
  }
}
