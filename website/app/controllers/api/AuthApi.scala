package controllers.api

import services.JsonUtil
import play.api.mvc.{Action, Controller}
import db.AccountDto
import models.frontend.SignInData

object AuthApi extends Controller {
  def authenticate = Action(parse.json) { request =>
      val signInData = JsonUtil.deserialize[(SignInData)](request.body.toString())

      AccountDto.getOfSignInInfo(signInData.username, signInData.password) match {
        case Some(account) =>
          Ok.withSession(
            request.session + ("accountId" -> account.id.get.toString)
          )

        case None =>
          NoContent
      }
  }
}
