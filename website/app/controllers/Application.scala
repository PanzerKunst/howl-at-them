package controllers

import play.api.mvc._
import db.{StateLegislatorDto, AccountDto, UsStateDto}
import models.Account
import play.api.Logger

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
     .map {
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

  def searchLegislators = Action {
    implicit request =>

      val selectedUsStateId = if (request.queryString.contains("selectedUsStateId"))
        Some(request.queryString.get("selectedUsStateId").get.head)
      else
        None

      val stateLegislators = selectedUsStateId match {
        case Some(usStateId) => StateLegislatorDto.getMatching(None, None, Some(usStateId))
        case None => List()
      }

      Ok(views.html.searchLegislators(UsStateDto.getAll, loggedInUser(session), selectedUsStateId, stateLegislators))
  }

  def stateLegislator(id: Int) = Action {
    implicit request =>

      StateLegislatorDto.getOfId(id) match {
        case Some(stateLegislator) => Ok(views.html.stateLegislator(stateLegislator, loggedInUser(session)))
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
        case None => */Ok(views.html.admin())/*
      }*/
  }

  def loggedInUser(session: Session): Option[Account] = {
    session.get("accountId") match {
      case Some(accountId) => AccountDto.getOfId(accountId.toLong)
      case None => None
    }
  }
}
