package controllers.api

import models.Account
import services.JsonUtil
import play.api.mvc.{Action, Controller}
import db.AccountDto

object AccountApi extends Controller {
  def create = Action(parse.json) {
    implicit request =>

      val account = JsonUtil.deserialize[Account](request.body.toString())
      AccountDto.create(account) match {
        case Some(id) => Ok(id.toString)
        case None => InternalServerError("Creation of an account did not return an ID!")
      }
  }

  def delete(username: String) = Action {
    implicit request =>

      AccountDto.getOfUsername(username) match {
        case None =>
          BadRequest("No account found for username '" + username + "'")

        case Some(account) =>
          AccountDto.delete(account)
          Ok
      }
  }
}
