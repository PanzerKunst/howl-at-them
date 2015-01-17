package controllers.api

import db.DbAdmin
import play.Play
import play.api.mvc.{Action, Controller}
import services.{VoteSmartCandidateService, VoteSmartCommitteeService, VoteSmartLeadershipService, VoteSmartService}

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
    if (VoteSmartService.isRunning) {
      Forbidden
    } else {
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
}
