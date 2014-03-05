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

  /* TODO: remove
  def update = Action(parse.json) {
    implicit request =>

      Application.loggedInUser(session) match {
        case None => {
          Logger.info("Account update attempt while not logged-in")
          Unauthorized
        }
        case Some(loggedInAccount) => {
          val account = JsonUtil.deserialize[Account](request.body.toString())

          if (account.id.get == loggedInAccount.id.get) {
            AccountDto.update(account)
            Ok
          }
          else
            Forbidden("Only your account, you may update.")
        }
      }
  }

  def getFirst = Action {
    implicit request =>

      if (request.queryString.contains("email")) {
        val email = request.queryString.get("email").get.head
        AccountDto.getOfEmail(email) match {
          case Some(account) => Ok(JsonUtil.serialize(new FrontendAccount(account)))
          case None => NoContent
        }
      } else {
        NoContent
      }
  } */
}
