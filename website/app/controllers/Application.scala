package controllers

import play.api.mvc._
import db.{PoliticalPartyDto, StateLegislatorTitleDto, AccountDto, UsStateDto}
import models.Account
import models.frontend.FrontendAccount

object Application extends Controller {

  def index = Action {
    implicit request =>

      loggedInUser(session) match {
        case Some(loggedInAccount) => Redirect(routes.Application.home)
        case None =>
          val action = if (request.queryString.contains("action"))
            Some(request.queryString.get("action").get.head)
          else
            None

          val emailAddress = if (request.queryString.contains("email")) {
            Some(request.queryString.get("email").get.head)
          } else {
            None
          }

          Ok(views.html.index(action, emailAddress))
      }
  }

  def logout = Action {
    implicit request =>

      Redirect(routes.Application.index).withNewSession
  }

  def join = Action {
    implicit request =>

      loggedInUser(session) match {
        case Some(loggedInAccount) => Redirect(routes.Application.home)
        case None => Ok(views.html.join(UsStateDto.getAll))
      }
  }

  def home = Action {
    implicit request =>

      loggedInUser(session) match {
        case None => Redirect(routes.Application.index)
        case Some(loggedInAccount) => Ok(views.html.home())
      }
  }

  def myAccount = Action {
    implicit request =>

      loggedInUser(session) match {
        case None => Redirect(routes.Application.index)
        case Some(loggedInAccount) => Ok(views.html.myAccount(new FrontendAccount(loggedInAccount), UsStateDto.getAll))
      }
  }

  def newStateLegislator = Action {
    implicit request =>

      loggedInUser(session) match {
        case None => Redirect(routes.Application.index)
        case Some(loggedInAccount) =>
          val from = if (request.queryString.contains("action"))
            Some(request.queryString.get("action").get.head)
          else
            None

          Ok(views.html.newStateLegislator(StateLegislatorTitleDto.getAll, PoliticalPartyDto.getAll, UsStateDto.getAll, from))
      }
  }

  def loggedInUser(session: Session): Option[Account] = {
    session.get("accountId") match {
      case Some(accountId) => AccountDto.getOfId(accountId.toLong)
      case None => None
    }
  }
}
