package services

import play.api.libs.json.{JsObject, Json}
import play.Play
import play.api.libs.ws.WS
import concurrent.Future
import models.StateAndDistrict
import play.api.Logger
import db.{StateLegislatorDto, UsStateDto}
import models.frontend.DetailedStateLegislator
import concurrent.ExecutionContext.Implicits.global

object GoogleCivicInformationService {
  val gciApiKey = Play.application().configuration().getString("google.civicinformation.apikey")

  def fetchStateLegislatorsForAddress(address: String): Future[List[DetailedStateLegislator]] = {
    WS.url("https://www.googleapis.com/civicinfo/us_v1/representatives/lookup")
      .withQueryString("key" -> Play.application().configuration().getString("google.civicinformation.apikey"))
      .withQueryString("includeOffices" -> "false")
      .withHeaders("Content-Type" -> "application/json")
      .withHeaders("X-JavaScript-User-Agent" -> "Google APIs Explorer")
      .post(Json.obj(
      "address" -> address
    )).map {
      response =>
        Logger.info("fetchDistrictsForAddress JSON reponse: " + response.json)

        (response.json \ "divisions").asOpt[JsObject] match {
          case Some(divisions) =>
            var stateLegislators: List[DetailedStateLegislator] = List()

            for (division <- divisions.values) {
              val divisionScope = (division \ "scope").as[String]
              if (divisionScope == "stateLower" || divisionScope == "stateUpper") {
                val usStateId = (response.json \ "normalizedInput" \ "state").as[String]

                UsStateDto.getOfId(usStateId) match {
                  case Some(usState) =>
                    val stateAndDistrict = StateAndDistrict(
                      usState,
                      districtNumberFromWebServiceResult(
                        (division \ "name").as[String]
                      )
                    )

                    for (stateLegislator <- StateLegislatorDto.getOfDistricts(List(stateAndDistrict))) {
                      if ((stateLegislator.isUpperHouse && divisionScope == "stateUpper") ||
                          (stateLegislator.isLowerHouse && divisionScope == "stateLower")) {
                        stateLegislators = stateLegislators :+ stateLegislator
                      }
                    }

                  case None =>
                }
              }
            }

            stateLegislators

          case None =>
            List()
        }
    }
  }

  private def districtNumberFromWebServiceResult(districtFromWebService: String): String = {
    // Get the last word
    val lastWord = districtFromWebService.split("\\s").last

    val castDistrict = try {
      lastWord.toInt
    }
    catch {
      case e: NumberFormatException => lastWord
    }

    castDistrict.toString
  }
}
