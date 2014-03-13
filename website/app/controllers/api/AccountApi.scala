package controllers.api

import models.Account
import services.JsonUtil
import play.api.mvc.{Action, Controller}
import db.AccountDto

object AccountApi extends Controller {
  def create = Action(parse.json) {
    implicit request =>

      val accountToCreate = JsonUtil.deserialize[Account](request.body.toString())
      AccountDto.create(accountToCreate) match {
        case Some(id) => Ok(id.toString)
        case None => InternalServerError("Creation of an account did not return an ID!")
      }
  }
}
