package controllers.api

import db.DbAdmin
import play.api.mvc.{Action, Controller}
import play.Play
import services.{VoteSmartCommitteeService, VoteSmartCandidateService, VoteSmartService, VoteSmartLeadershipService}

object DbAdminApi extends Controller {
  def reCreateNonVoteSmartTables = Action { request =>
      if (request.queryString.contains("key") &&
        request.queryString.get("key").get.head == Play.application().configuration().getString("application.secret")) {
        DbAdmin.reCreateNonVoteSmartTables()
        DbAdmin.initData()
        Created
      }
      else
        Forbidden
  }

  def updateVoteSmartData() = Action { request =>
      DbAdmin.reCreateTempVoteSmartTables()

      VoteSmartCandidateService.fetchCandidates()

      while (VoteSmartService.isRunning) {
        //Pause for 100 ms
        Thread.sleep(100)
      }

      VoteSmartLeadershipService.fetchLeaderships()

      while (VoteSmartService.isRunning) {
        Thread.sleep(100)
      }

      VoteSmartCommitteeService.fetchCommittees()

      while (VoteSmartService.isRunning) {
        Thread.sleep(100)
      }

      DbAdmin.replaceVoteSmartTables()

      Created
  }
}
