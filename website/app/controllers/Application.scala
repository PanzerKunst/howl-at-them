package controllers

import api.CommitteeApi
import play.api.mvc._
import db._
import models.{SupportLevel, Chamber}
import services.{GoogleCivicInformationService, VoteSmartService}
import concurrent.ExecutionContext.Implicits.global
import concurrent.Future
import models.frontend.DetailedStateLegislator
import models.frontend.WhipCount
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

  def stateReports = Action {
    implicit request =>

      val action = if (request.queryString.contains("action"))
        Some(request.queryString.get("action").get.head)
      else
        None

      val selectedUsStateId = session.get("selectedUsStateId").getOrElse("AK")
      val detailedLegislatorsForThisState = StateLegislatorDto.getOfStateId(selectedUsStateId)

      val whipCountForHouse = calculateWhipCountForChamber(Chamber.HOUSE, detailedLegislatorsForThisState)
      val whipCountForSenate = calculateWhipCountForChamber(Chamber.SENATE, detailedLegislatorsForThisState)
      val whipCountForBoth = calculateWhipCountForBothChambers(whipCountForHouse, whipCountForSenate)

      Ok(views.html.stateReports(UsStateDto.all, isAdmin(session), selectedUsStateId, whipCountForHouse, whipCountForSenate, whipCountForBoth, action))
        .withHeaders(doNotCachePage: _*)
  }

  def searchLegislators = Action {
    implicit request =>

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

      Ok(views.html.searchLegislators(UsStateDto.all, LeadershipPositionDto.getInState(selectedUsStateId), committeeNamesInState, isAdmin(session), selectedUsStateId, selectedLeadershipPositionId, session.get("selectedCommitteeName")))
        .withSession(newSession)
        .withHeaders(doNotCachePage: _*)
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

  private def calculateWhipCountForChamber(chamber: Chamber, detailedLegislatorsForThisState: List[DetailedStateLegislator]): List[WhipCount] = {
    var nbLegislators: Int = 0
    var nbLegislatorsSupportive: Int = 0
    var nbLegislatorsNeedingConvincing: Int = 0
    var nbLegislatorsNotSupportive: Int = 0

    for (stateLegislator <- detailedLegislatorsForThisState) {
      if (stateLegislator.getChamber == chamber) {
        nbLegislators = nbLegislators + 1

        stateLegislator.reports.headOption match {
          case Some(report) =>
            report.supportLevel match {
              case "SUPPORTIVE" => nbLegislatorsSupportive = nbLegislatorsSupportive + 1
              case "NEEDS_CONVINCING" => nbLegislatorsNeedingConvincing = nbLegislatorsNeedingConvincing + 1
              case "NOT_SUPPORTIVE" => nbLegislatorsNotSupportive = nbLegislatorsNotSupportive + 1
              case otherString =>
            }

          case None =>
        }
      }
    }

    // Supportive
    val whipCountSupportive = WhipCount(SupportLevel.SUPPORTIVE,
      nbLegislatorsSupportive,
      (nbLegislatorsSupportive.toDouble / nbLegislators * 100).round.toInt)

    // Needing convincing
    val whipCountNeedingConvincing = WhipCount(SupportLevel.NEEDS_CONVINCING,
      nbLegislatorsNeedingConvincing,
      (nbLegislatorsNeedingConvincing.toDouble / nbLegislators * 100).round.toInt)

    // Not supportive
    val whipCountNotSupportive = WhipCount(SupportLevel.NOT_SUPPORTIVE,
      nbLegislatorsNotSupportive,
      (nbLegislatorsNotSupportive.toDouble / nbLegislators * 100).round.toInt)

    // Unknown
    val nbLegislatorsWhoseSupportLevelIsUnknown = nbLegislators - nbLegislatorsSupportive - nbLegislatorsNeedingConvincing - nbLegislatorsNotSupportive
    val whipCountUnknown = WhipCount(SupportLevel.UNKNOWN,
      nbLegislatorsWhoseSupportLevelIsUnknown,
      (nbLegislatorsWhoseSupportLevelIsUnknown.toDouble / nbLegislators * 100).round.toInt)

    List(whipCountSupportive,
      whipCountNeedingConvincing,
      whipCountNotSupportive,
      whipCountUnknown)
  }

  private def calculateWhipCountForBothChambers(whipCountForHouse: List[WhipCount], whipCountForSenate: List[WhipCount]): List[WhipCount] = {
    val nbLegislatorsSupportive = whipCountForHouse.apply(0).count + whipCountForSenate.apply(0).count
    val nbLegislatorsNeedingConvincing = whipCountForHouse.apply(1).count + whipCountForSenate.apply(1).count
    val nbLegislatorsNotSupportive = whipCountForHouse.apply(2).count + whipCountForSenate.apply(2).count
    val nbLegislatorsUnknown = whipCountForHouse.apply(3).count + whipCountForSenate.apply(3).count
    val nbLegislators = nbLegislatorsSupportive + nbLegislatorsNeedingConvincing + nbLegislatorsNotSupportive + nbLegislatorsUnknown

    // Supportive
    val whipCountSupportive = WhipCount(SupportLevel.SUPPORTIVE,
      nbLegislatorsSupportive,
      (nbLegislatorsSupportive.toDouble / nbLegislators * 100).round.toInt)

    // Needing convincing
    val whipCountNeedingConvincing = WhipCount(SupportLevel.NEEDS_CONVINCING,
      nbLegislatorsNeedingConvincing,
      (nbLegislatorsNeedingConvincing.toDouble / nbLegislators * 100).round.toInt)

    // Not supportive
    val whipCountNotSupportive = WhipCount(SupportLevel.NOT_SUPPORTIVE,
      nbLegislatorsNotSupportive,
      (nbLegislatorsNotSupportive.toDouble / nbLegislators * 100).round.toInt)

    // Unknown
    val nbLegislatorsWhoseSupportLevelIsUnknown = nbLegislators - nbLegislatorsSupportive - nbLegislatorsNeedingConvincing - nbLegislatorsNotSupportive
    val whipCountUnknown = WhipCount(SupportLevel.UNKNOWN,
      nbLegislatorsWhoseSupportLevelIsUnknown,
      (nbLegislatorsWhoseSupportLevelIsUnknown.toDouble / nbLegislators * 100).round.toInt)

    List(whipCountSupportive,
      whipCountNeedingConvincing,
      whipCountNotSupportive,
      whipCountUnknown)
  }
}
