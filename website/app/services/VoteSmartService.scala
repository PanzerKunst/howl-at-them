package services

import play.Play

object VoteSmartService {
  val voteSmartApiKey = Play.application().configuration().getString("votesmart.apikey")
  var isRunning = false
}
