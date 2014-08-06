package controllers.api

import play.api.mvc.{Action, Controller}
import models.Report
import services.JsonUtil
import db.ReportDto
import controllers.Application

object ReportApi extends Controller {
  def create = Action(parse.json) { request =>
      val report = JsonUtil.deserialize[Report](request.body.toString())
      ReportDto.create(report) match {
        case Some(id) => Ok(id.toString)
        case None => InternalServerError("Creation of a report did not return an ID!")
      }
  }

  def update = Action(parse.json) { request =>
      val report = JsonUtil.deserialize[Report](request.body.toString())

      report.id match {
        case Some(id) =>
          ReportDto.getOfId(id) match {
            case None =>
              BadRequest("No report found for id '" + id + "'")
            case Some(existingReport) =>
              ReportDto.update(report)
              Ok
          }
        case None =>
          BadRequest("Report ID missing")
      }
  }

  def delete(id: Long) = Action { request =>
      ReportDto.getOfId(id) match {
        case None =>
          BadRequest("No report found for id '" + id + "'")
        case Some(report) =>
          ReportDto.delete(report)
          Ok
      }
  }
}
