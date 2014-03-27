package services

import play.api.libs.json.{JsValue, JsObject, Json}
import play.Play
import play.api.libs.ws.WS
import concurrent.Future
import concurrent.ExecutionContext.Implicits.global
import models.StateAndDistrict
import play.api.Logger
import db.UsStateDto

object GoogleCivicInformationService {
  val gciApiKey = Play.application().configuration().getString("google.civicinformation.apikey")

  def fetchDistrictsForAddress(address: String): Future[List[StateAndDistrict]] = {
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
            var statesAndDistricts: List[StateAndDistrict] = List()

            for (division <- divisions.values) {
              val divisionScope = (division \ "scope").as[String]
              if (divisionScope == "stateLower" || divisionScope == "stateUpper") {
                val usStateId = (response.json \ "normalizedInput" \ "state").as[String]

                UsStateDto.getOfId(usStateId) match {
                  case Some(usState) =>
                    statesAndDistricts = statesAndDistricts :+ StateAndDistrict(
                      usState,
                      (division \ "name").as[String]
                    )
                  case None =>
                }
              }
            }

            statesAndDistricts.map {
              district =>
                StateAndDistrict(district.usState,
                  districtNumberFromWebServiceResult(district.district)
                )
            }

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
