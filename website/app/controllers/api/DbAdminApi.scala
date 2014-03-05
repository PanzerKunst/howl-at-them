package controllers.api

import db.DbAdmin
import play.api.mvc.{Action, Controller}
import play.Play
import services.VoteSmartCandidateService

object DbAdminApi extends Controller {
  def reCreateNonVoteSmartTables = Action {
    implicit request =>

      if (request.queryString.contains("key") &&
        request.queryString.get("key").get.head == Play.application().configuration().getString("application.secret")) {
        DbAdmin.reCreateNonVoteSmartTables()
        DbAdmin.initData()
        Created
      }
      else
        Forbidden
  }

  def updateVoteSmartData() = Action {
    implicit request =>

      DbAdmin.reCreateTempVoteSmartTables()
      VoteSmartCandidateService.fetchCandidates()
      DbAdmin.replaceVoteSmartTables()

      Ok
  }
}
