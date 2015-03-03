package controllers

import controllers.api.CommitteeApi
import db._
import models.Account
import play.api.mvc._
import services.{GoogleCivicInformationService, VoteSmartService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Application extends Controller {

  val doNotCachePage = Array(CACHE_CONTROL -> "no-cache, no-store")

  def index = Action { request =>
    Ok(views.html.index())
  }

  def logOut = Action { request =>
    Redirect(routes.Application.index).withSession(request.session - "accountId")
  }

  def stateLegislators = Action { request =>
    val action = if (request.queryString.contains("action"))
      Some(request.queryString.get("action").get.head)
    else
      None

    var isAdministrator = false
    var newSession = request.session

    if (request.queryString.contains("role") && request.queryString.get("role").get.head == "admin") {
      isAdministrator = true
      newSession = request.session + ("accountId" -> "1")
    } else {
      isAdministrator = isAdmin(request.session)
    }

    var selectedUsStateId = ""

    if (request.queryString.contains("usStateId")) {
      selectedUsStateId = request.queryString.get("usStateId").get.head
      newSession = newSession + ("selectedUsStateId" -> selectedUsStateId)
    } else {
      selectedUsStateId = request.session.get("selectedUsStateId").getOrElse("AL")
    }

    val selectedNbDaysSinceLastReport = request.session.get("selectedNbDaysSinceLastReport") match {
      case Some(nbDays) => Some(nbDays.toInt)
      case None => None
    }

    val selectedLeadershipPositionId = request.session.get("selectedLeadershipPositionId") match {
      case Some(positionId) => Some(positionId.toInt)
      case None => None
    }

    val committeesInState = CommitteeDto.getInState(selectedUsStateId)
    val committeeNamesInState = CommitteeApi.committeeNamesWithoutDuplicates(committeesInState)

    Ok(views.html.stateLegislators(UsStateDto.all, isAdministrator, selectedUsStateId, action, LeadershipPositionDto.getInState(selectedUsStateId), committeeNamesInState, selectedNbDaysSinceLastReport, request.session.get("selectedChamberOrTargetSearchCriteria"), selectedLeadershipPositionId, request.session.get("selectedCommitteeName")))
      .withSession(newSession)
  }

  def stateLegislator(id: Int) = Action { request =>
    val action = if (request.queryString.contains("action"))
      Some(request.queryString.get("action").get.head)
    else
      None

    StateLegislatorDto.getOfId(id) match {
      case Some(detailedStateLegislator) =>
        Ok(views.html.stateLegislator(detailedStateLegislator, action, isAdmin(request.session)))
          .withHeaders(doNotCachePage: _*)
      case None =>
        NotFound
    }
  }

  def adminLogin = Action { request =>
    loggedInUser(request.session) match {
      case Some(loggedInAccount) => Redirect(routes.Application.admin)
      case None => Ok(views.html.adminLogin())
    }
  }

  def admin = Action { request =>
    loggedInUser(request.session) match {
      case Some(loggedInAccount) => Ok(views.html.admin(VoteSmartService.isRunning))
      case None => Redirect(routes.Application.adminLogin)
    }
  }

  def findYourLegislator = Action.async { request =>
    if (request.queryString.contains("address")) {
      val address = request.queryString.get("address").get.head
      GoogleCivicInformationService.fetchStateLegislatorsForAddress(address).map {
        yourLegislators =>
          Ok(views.html.findYourLegislator(isAdmin(request.session), UsStateDto.all, Some(address), yourLegislators))
      }
    } else {
      Future {
        Ok(views.html.findYourLegislator(isAdmin(request.session), UsStateDto.all, None, List()))
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
