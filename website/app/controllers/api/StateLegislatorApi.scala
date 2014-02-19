package controllers.api

import models.{StateLegislator, Account}
import services.JsonUtil
import play.api.mvc.{Action, Controller}
import db.{StateLegislatorDto, AccountDto}
import models.frontend.FrontendAccount
import controllers.Application
import play.api.Logger

object StateLegislatorApi extends Controller {
  def create = Action(parse.json) {
    implicit request =>

      val stateLegislatorToCreate = JsonUtil.deserialize[StateLegislator](request.body.toString())
      StateLegislatorDto.create(stateLegislatorToCreate) match {
        case Some(id) => Ok(id.toString)
        case None => InternalServerError("Creation of a state legislator did not return an ID!")
      }
  }
}
