package controllers

import api.CommitteeApi
import play.api.mvc._
import db._
import services.{GoogleCivicInformationService, VoteSmartService}
import concurrent.ExecutionContext.Implicits.global
import concurrent.Future
import scala.Some
import models.Account

object Application extends Controller {

  val doNotCachePage = Array((CACHE_CONTROL -> "no-cache, no-store"))

  def index = Action {
    implicit request =>

      Ok(views.html.index())
  }

  def logOut = Action {
    implicit request =>

      Redirect(routes.Application.index).withSession(session - "accountId")
  }

  def stateLegislators = Action {
    implicit request =>

      val action = if (request.queryString.contains("action"))
        Some(request.queryString.get("action").get.head)
      else
        None

      val (selectedUsStateId, newSession) = if (request.queryString.contains("usStateId")) {
        val selectedUsStateId = request.queryString.get("usStateId").get.head
        (selectedUsStateId, session + ("selectedUsStateId" -> selectedUsStateId))
      } else {
        val selectedUsStateId = session.get("selectedUsStateId").getOrElse("AK")
        (selectedUsStateId, session)
      }

      val selectedLeadershipPositionId = session.get("selectedLeadershipPositionId") match {
        case Some(positionId) => Some(positionId.toInt)
        case None => None
      }

      val committeesInState = CommitteeDto.getInState(selectedUsStateId)
      val committeeNamesInState = CommitteeApi.committeeNamesWithoutDuplicates(committeesInState)

      Ok(views.html.stateLegislators(UsStateDto.all, isAdmin(session), selectedUsStateId, action, LeadershipPositionDto.getInState(selectedUsStateId), committeeNamesInState, selectedLeadershipPositionId, session.get("selectedCommitteeName")))
        .withSession(newSession)
  }

  def stateLegislator(id: Int) = Action {
    implicit request =>

      val action = if (request.queryString.contains("action"))
        Some(request.queryString.get("action").get.head)
      else
        None

      StateLegislatorDto.getOfId(id) match {
        case Some(detailedStateLegislator) =>
          Ok(views.html.stateLegislator(detailedStateLegislator, action, isAdmin(session)))
            .withHeaders(doNotCachePage: _*)
        case None =>
          NotFound
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
