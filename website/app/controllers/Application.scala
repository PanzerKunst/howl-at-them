package controllers

import play.api.mvc._
import db.{StateLegislatorDto, AccountDto, UsStateDto}
import models.Account
import services.{GoogleCivicInformationService, VoteSmartService}
import concurrent.ExecutionContext.Implicits.global
import concurrent.Future

object Application extends Controller {

  def index = Action {
    implicit request =>

      Ok(views.html.index())
  }

  def logOut = Action {
    implicit request =>

      Redirect(routes.Application.index).withSession(session - "accountId")
  }

  def stateReports = Action {
    implicit request =>
      val action = if (request.queryString.contains("action"))
        Some(request.queryString.get("action").get.head)
      else
        None

      if (request.queryString.contains("usStateId")) {
        val selectedUsStateId = request.queryString.get("usStateId").get.head
        val detailedLegislatorsForThisState = StateLegislatorDto.getOfStateId(selectedUsStateId)

        val firstLegislator = detailedLegislatorsForThisState.headOption

        val nextLegislators = for (i <- 1 to detailedLegislatorsForThisState.length - 1) yield detailedLegislatorsForThisState.apply(i)

        Ok(views.html.stateReports(UsStateDto.all, isAdmin(session), Some(selectedUsStateId), firstLegislator, nextLegislators.toList, action)).withSession(
          session + ("selectedUsStateId" -> selectedUsStateId)
        )
      } else {
        session.get("selectedUsStateId") match {
          case Some(selectedUsStateId) =>
            val detailedLegislatorsForThisState = StateLegislatorDto.getOfStateId(selectedUsStateId)

            val firstLegislator = detailedLegislatorsForThisState.headOption

            val nextLegislators = for (i <- 1 to detailedLegislatorsForThisState.length - 1) yield detailedLegislatorsForThisState.apply(i)

            Ok(views.html.stateReports(UsStateDto.all, isAdmin(session), Some(selectedUsStateId), firstLegislator, nextLegislators.toList, action))

          case None => Ok(views.html.stateReports(UsStateDto.all, isAdmin(session), None, None, List(), action))
        }
      }
  }

  def searchLegislators = Action {
    implicit request =>
      Ok(views.html.searchLegislators(UsStateDto.all, isAdmin(session)))
  }

  def stateLegislator(id: Int) = Action {
    implicit request =>

      val action = if (request.queryString.contains("action"))
        Some(request.queryString.get("action").get.head)
      else
        None

      StateLegislatorDto.getOfId(id) match {
        case Some(detailedStateLegislator) => Ok(views.html.stateLegislator(detailedStateLegislator, action, isAdmin(session)))
        case None => NotFound
      }
  }

  def adminLogin = Action {
    implicit request =>

      loggedInUser(session) match {
        case Some(loggedInAccount) => Redirect(routes.Application.admin)
        case None => Ok(views.html.adminLogin())
      }
  }

  def admin = Action {
    implicit request =>

      loggedInUser(session) match {
        case Some(loggedInAccount) => Ok(views.html.admin(VoteSmartService.isRunning))
        case None => Redirect(routes.Application.adminLogin)
      }
  }

  def findYourLegislator = Action.async {
    implicit request =>
      if (request.queryString.contains("address")) {
        val address = request.queryString.get("address").get.head
        GoogleCivicInformationService.fetchStateLegislatorsForAddress(address).map {
          yourLegislators =>
            Ok(views.html.findYourLegislator(isAdmin(session), UsStateDto.all, Some(address), yourLegislators))
        }
      } else {
        Future {
          Ok(views.html.findYourLegislator(isAdmin(session), UsStateDto.all, None, List()))
        }
      }
  }

  private def isAdmin(session: Session): Boolean = {
    loggedInUser(session).isDefined
  }

  private def loggedInUser(session: Session): Option[Account] = {
    session.get("accountId") match {
      case Some(accountId) => AccountDto.getOfId(accountId.toLong)
      case None => None
    }
  }
}
