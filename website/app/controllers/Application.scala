package controllers

import api.CommitteeApi
import play.api.mvc._
import db._
import models.{SupportLevel, Chamber, Account}
import services.{GoogleCivicInformationService, VoteSmartService}
import concurrent.ExecutionContext.Implicits.global
import concurrent.Future
import models.frontend.{WhipCount, WhipCountForChamber, DetailedStateLegislator}
import models.frontend.WhipCount
import models.frontend.WhipCountForChamber
import scala.Some
import models.Account

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

        Ok(views.html.stateReports(UsStateDto.all, isAdmin(session), Some(selectedUsStateId), Some(calculateWhipCountPerChamber(detailedLegislatorsForThisState)), firstLegislator, nextLegislators.toList, action)).withSession(
          session + ("selectedUsStateId" -> selectedUsStateId)
        )
      } else {
        session.get("selectedUsStateId") match {
          case Some(selectedUsStateId) =>
            val detailedLegislatorsForThisState = StateLegislatorDto.getOfStateId(selectedUsStateId)

            val firstLegislator = detailedLegislatorsForThisState.headOption

            val nextLegislators = for (i <- 1 to detailedLegislatorsForThisState.length - 1) yield detailedLegislatorsForThisState.apply(i)

            Ok(views.html.stateReports(UsStateDto.all, isAdmin(session), Some(selectedUsStateId), Some(calculateWhipCountPerChamber(detailedLegislatorsForThisState)), firstLegislator, nextLegislators.toList, action))

          case None => Ok(views.html.stateReports(UsStateDto.all, isAdmin(session), None, None, None, List(), action))
        }
      }
  }

  def searchLegislators = Action {
    implicit request =>

      if (request.queryString.contains("usStateId")) {
        val selectedUsStateId = request.queryString.get("usStateId").get.head

        val selectedLeadershipPositionId = session.get("selectedLeadershipPositionId") match {
          case Some(positionId) => Some(positionId.toInt)
          case None => None
        }

        val committeesInState = CommitteeDto.getInState(selectedUsStateId)
        val committeeNamesInState = CommitteeApi.committeeNamesWithoutDuplicates(committeesInState)

        val selectedIsAPriorityTarget = session.get("selectedIsAPriorityTarget") match {
          case Some(isAPriorityTarget) => isAPriorityTarget.toBoolean
          case None => false
        }

        Ok(views.html.searchLegislators(UsStateDto.all, LeadershipPositionDto.getInState(selectedUsStateId), committeeNamesInState, isAdmin(session), Some(selectedUsStateId), selectedLeadershipPositionId, session.get("selectedCommitteeName"), selectedIsAPriorityTarget)).withSession(
          session + ("selectedUsStateId" -> selectedUsStateId)
        )
      } else {
        val selectedUsStateId = session.get("selectedUsStateId").getOrElse("AK")

        val selectedLeadershipPositionId = session.get("selectedLeadershipPositionId") match {
          case Some(positionId) => Some(positionId.toInt)
          case None => None
        }

        val committeesInState = CommitteeDto.getInState(selectedUsStateId)
        val committeeNamesInState = CommitteeApi.committeeNamesWithoutDuplicates(committeesInState)

        val selectedIsAPriorityTarget = session.get("selectedIsAPriorityTarget") match {
          case Some(isAPriorityTarget) => isAPriorityTarget.toBoolean
          case None => false
        }

        Ok(views.html.searchLegislators(UsStateDto.all, LeadershipPositionDto.getInState(selectedUsStateId), committeeNamesInState, isAdmin(session), Some(selectedUsStateId), selectedLeadershipPositionId, session.get("selectedCommitteeName"), selectedIsAPriorityTarget))
      }
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

  private def calculateWhipCountPerChamber(detailedLegislatorsForThisState: List[DetailedStateLegislator]): (WhipCountForChamber, WhipCountForChamber) = {
    var nbHouseLegislators: Int = 0
    var nbHouseLegislatorsSupportive: Int = 0
    var nbHouseLegislatorsNeedingConvincing: Int = 0
    var nbHouseLegislatorsNotSupportive: Int = 0

    var nbSenateLegislators: Int = 0
    var nbSenateLegislatorsSupportive: Int = 0
    var nbSenateLegislatorsNeedingConvincing: Int = 0
    var nbSenateLegislatorsNotSupportive: Int = 0

    for (stateLegislator <- detailedLegislatorsForThisState) {
      if (stateLegislator.getChamber == Chamber.HOUSE) {
        nbHouseLegislators = nbHouseLegislators + 1

        stateLegislator.reports.headOption match {
          case Some(report) =>
            report.supportLevel match {
              case Some("SUPPORTIVE") => nbHouseLegislatorsSupportive = nbHouseLegislatorsSupportive + 1
              case Some("NEEDS_CONVINCING") => nbHouseLegislatorsNeedingConvincing = nbHouseLegislatorsNeedingConvincing + 1
              case Some("NOT_SUPPORTIVE") => nbHouseLegislatorsNotSupportive = nbHouseLegislatorsNotSupportive + 1
              case Some(otherString) =>
              case None =>
            }

          case None =>
        }
      } else if (stateLegislator.getChamber == Chamber.SENATE) {
        nbSenateLegislators = nbSenateLegislators + 1

        stateLegislator.reports.headOption match {
          case Some(report) =>
            report.supportLevel match {
              case Some("SUPPORTIVE") => nbSenateLegislatorsSupportive = nbSenateLegislatorsSupportive + 1
              case Some("NEEDS_CONVINCING") => nbSenateLegislatorsNeedingConvincing = nbSenateLegislatorsNeedingConvincing + 1
              case Some("NOT_SUPPORTIVE") => nbSenateLegislatorsNotSupportive = nbSenateLegislatorsNotSupportive + 1
              case Some(otherString) =>
              case None =>
            }

          case None =>
        }
      }
    }

    // House, supportive
    val whipCountHouseSupportive = WhipCount(Some(SupportLevel.SUPPORTIVE),
      nbHouseLegislatorsSupportive,
      (nbHouseLegislatorsSupportive.toDouble / nbHouseLegislators * 100).toInt)

    // House, needing convincing
    val whipCountHouseNeedingConvincing = WhipCount(Some(SupportLevel.NEEDS_CONVINCING),
      nbHouseLegislatorsNeedingConvincing,
      (nbHouseLegislatorsNeedingConvincing.toDouble / nbHouseLegislators * 100).toInt)

    // House, not supportive
    val whipCountHouseNotSupportive = WhipCount(Some(SupportLevel.NOT_SUPPORTIVE),
      nbHouseLegislatorsNotSupportive,
      (nbHouseLegislatorsNotSupportive.toDouble / nbHouseLegislators * 100).toInt)

    // House, unknown
    val nbHouseLegislatorsWhoseSupportLevelIsUnknown = nbHouseLegislators - nbHouseLegislatorsSupportive - nbHouseLegislatorsNeedingConvincing - nbHouseLegislatorsNotSupportive
    val whipCountHouseUnknown = WhipCount(None,
      nbHouseLegislatorsWhoseSupportLevelIsUnknown,
      (nbHouseLegislatorsWhoseSupportLevelIsUnknown.toDouble / nbHouseLegislators * 100).toInt)

    // Senate, supportive
    val whipCountSenateSupportive = WhipCount(Some(SupportLevel.SUPPORTIVE),
      nbSenateLegislatorsSupportive,
      (nbSenateLegislatorsSupportive.toDouble / nbSenateLegislators * 100).toInt)

    // Senate, needing convincing
    val whipCountSenateNeedingConvincing = WhipCount(Some(SupportLevel.NEEDS_CONVINCING),
      nbSenateLegislatorsNeedingConvincing,
      (nbSenateLegislatorsNeedingConvincing.toDouble / nbSenateLegislators * 100).toInt)

    // Senate, not supportive
    val whipCountSenateNotSupportive = WhipCount(Some(SupportLevel.NOT_SUPPORTIVE),
      nbSenateLegislatorsNotSupportive,
      (nbSenateLegislatorsNotSupportive.toDouble / nbSenateLegislators * 100).toInt)

    // Senate, unknown
    val nbSenateLegislatorsWhoseSupportLevelIsUnknown = nbSenateLegislators - nbSenateLegislatorsSupportive - nbSenateLegislatorsNeedingConvincing - nbSenateLegislatorsNotSupportive
    val whipCountSenateUnknown = WhipCount(None,
      nbSenateLegislatorsWhoseSupportLevelIsUnknown,
      (nbSenateLegislatorsWhoseSupportLevelIsUnknown.toDouble / nbSenateLegislators * 100).toInt)

    val houseWhipCount = WhipCountForChamber(Chamber.HOUSE,
      List(whipCountHouseSupportive,
        whipCountHouseNeedingConvincing,
        whipCountHouseNotSupportive,
        whipCountHouseUnknown)
    )

    val senateWhipCount = WhipCountForChamber(Chamber.SENATE,
      List(whipCountSenateSupportive,
        whipCountSenateNeedingConvincing,
        whipCountSenateNotSupportive,
        whipCountSenateUnknown)
    )

    (houseWhipCount, senateWhipCount)
  }
}
