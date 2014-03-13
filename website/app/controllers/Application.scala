package controllers

import play.api.mvc._
import db.{StateLegislatorDto, AccountDto, UsStateDto}
import models.Account
import services.VoteSmartService

object Application extends Controller {

  def index = Action {
    implicit request =>

    /* TODO implicit val context = scala.concurrent.ExecutionContext.Implicits.global

   // Google Civic Information
   WS.url("https://www.googleapis.com/civicinfo/us_v1/voterinfo/2000/lookup")
     .withQueryString("key" -> Play.application().configuration().getString("google.civicinformation.apikey"))
     .withHeaders("Content-Type" -> "application/json")
     .withHeaders("X-JavaScript-User-Agent" -> "Google APIs Explorer")
     .post(Json.obj(
       "address" -> "410 Market St, Chapel Hill, NC"
     ))
     map {
     response =>
       val contests = (response.json \ "contests").as[List[JsObject]]
       for (contest <- contests) {
         val district = (contest \ "district").as[JsValue]
         val districtScope = (district \ "scope").as[String]
         if (districtScope == "stateLower" || districtScope == "stateUpper") {
           val districtName = (district \ "name").as[String]
         }
       }
   } */

      Ok(views.html.index(UsStateDto.getAll))
  }

  def logOut = Action {
    implicit request =>

      Redirect(routes.Application.index).withNewSession
  }

  def stateReports = Action {
    implicit request =>

      if (request.queryString.contains("usStateId")) {
        val selectedUsStateId = request.queryString.get("usStateId").get.head
        val detailedLegislatorsForThisState = StateLegislatorDto.getOfStateId(selectedUsStateId)

        val firstLegislator = detailedLegislatorsForThisState.headOption

        val nextLegislators = for (i <- 1 to detailedLegislatorsForThisState.length-1) yield detailedLegislatorsForThisState.apply(i)

        Ok(views.html.stateReports(UsStateDto.getAll, loggedInUser(session), Some(selectedUsStateId), firstLegislator, nextLegislators.toList)).withSession(
          session + ("selectedUsStateId" -> selectedUsStateId)
        )
      } else {
        session.get("selectedUsStateId") match {
          case Some(selectedUsStateId) =>
            val detailedLegislatorsForThisState = StateLegislatorDto.getOfStateId(selectedUsStateId)

            val firstLegislator = detailedLegislatorsForThisState.headOption

            val nextLegislators = for (i <- 1 to detailedLegislatorsForThisState.length-1) yield detailedLegislatorsForThisState.apply(i)

            Ok(views.html.stateReports(UsStateDto.getAll, loggedInUser(session), Some(selectedUsStateId), firstLegislator, nextLegislators.toList))

          case None => Ok(views.html.stateReports(UsStateDto.getAll, loggedInUser(session), None, None, List()))
        }
      }
  }

  def searchLegislators = Action {
    implicit request =>
      Ok(views.html.searchLegislators(UsStateDto.getAll, loggedInUser(session)))
  }

  def stateLegislator(id: Int) = Action {
    implicit request =>

      val action = if (request.queryString.contains("action"))
        Some(request.queryString.get("action").get.head)
      else
        None

      StateLegislatorDto.getOfId(id) match {
        case Some(detailedStateLegislator) => Ok(views.html.stateLegislator(detailedStateLegislator, detailedStateLegislator.reports.headOption, detailedStateLegislator.reports, action, loggedInUser(session)))
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

    /* TODO loggedInUser(session) match {
 case Some(loggedInAccount) => Redirect(routes.Application.adminLogin)
 case None => */

      Ok(views.html.admin(VoteSmartService.isRunning)) /*
      }*/
  }

  def loggedInUser(session: Session): Option[Account] = {
    session.get("accountId") match {
      case Some(accountId) => AccountDto.getOfId(accountId.toLong)
      case None => None
    }
  }
}
