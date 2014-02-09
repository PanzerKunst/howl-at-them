package controllers.api

import services.JsonUtil
import play.api.mvc.{Action, Controller}
import db.AccountDto
import models.frontend.SignInData

object AuthApi extends Controller {
  def authenticate = Action(parse.json) {
    implicit request =>

      val signInData = JsonUtil.deserialize[(SignInData)](request.body.toString())

      AccountDto.getOfSignInInfo(signInData.emailAddress, signInData.password) match {
        case Some(account) =>
          Ok.withSession(
            session + ("accountId" -> account.id.get.toString)
          )
        case None => NoContent
      }
  }
}
