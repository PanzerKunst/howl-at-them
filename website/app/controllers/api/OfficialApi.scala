package controllers.api

import models.{Official, Account}
import services.JsonUtil
import play.api.mvc.{Action, Controller}
import db.{OfficialDto, AccountDto}
import models.frontend.FrontendAccount
import controllers.Application
import play.api.Logger

object OfficialApi extends Controller {
  def create = Action(parse.json) {
    implicit request =>

      val officialToCreate = JsonUtil.deserialize[Official](request.body.toString())
      OfficialDto.create(officialToCreate) match {
        case Some(id) => Ok(id.toString)
        case None => InternalServerError("Creation of an official did not return an ID!")
      }
  }
}
