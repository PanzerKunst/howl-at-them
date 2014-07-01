package services

import play.api.libs.json.{JsObject, Json}
import play.Play
import play.api.libs.ws.WS
import concurrent.Future
import models.{UsState, Chamber, StateAndDistrict}
import play.api.Logger
import db.{StateLegislatorDto, UsStateDto}
import models.frontend.DetailedStateLegislator
import concurrent.ExecutionContext.Implicits.global

object GoogleCivicInformationService {
  val gciApiKey = Play.application().configuration().getString("google.civicinformation.apikey")
  val divisionScopeHouse = "stateLower"
  val divisionScopeSenate = "stateUpper"

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
              (division \ "scope").asOpt[String] match {
                case Some(divisionScope) =>
                  if (divisionScope == divisionScopeHouse || divisionScope == divisionScopeSenate) {
                    val usStateId = (response.json \ "normalizedInput" \ "state").as[String]

                    UsStateDto.getOfId(usStateId) match {
                      case Some(usState) =>
                        val stateAndDistrict = StateAndDistrict(
                          usState,
                          voteSmartDistrictFromWebServiceResult(
                            (division \ "name").as[String],
                            usState,
                            divisionScope
                          )
                        )

                        for (stateLegislator <- StateLegislatorDto.getOfDistricts(List(stateAndDistrict))) {
                          if ((stateLegislator.getChamber == Chamber.SENATE && divisionScope == divisionScopeSenate) ||
                            (stateLegislator.getChamber == Chamber.HOUSE && divisionScope == divisionScopeHouse)) {
                            stateLegislators = stateLegislators :+ stateLegislator
                          }
                        }

                      case None =>
                    }
                  }
                case None =>
              }
            }

            stateLegislators

          case None =>
            List()
        }
    }
  }

  private def voteSmartDistrictFromWebServiceResult(districtFromWebService: String, usState: UsState, divisionScope: String): String = {
    val districtFromWS = districtFromWebService.replaceAll("\\s0(\\d)([A-Z])", " $1$2")

    if (usState.id == "MA") {
      districtFromWS.replaceAll("^Massachusetts\\s", "")
        .replaceAll("\\sdistrict$", "")

        .replaceAll("&", "and")

        .replaceAll("1st", "first")
        .replaceAll("2nd", "second")
        .replaceAll("3rd", "third")
        .replaceAll("4th", "fourth")
        .replaceAll("5th", "fifth")
        .replaceAll("6th", "sixth")
        .replaceAll("7th", "seventh")
        .replaceAll("8th", "eigth")
        .replaceAll("9th", "ninth")

        .replaceAll("10th", "tenth")
        .replaceAll("11th", "eleventh")
        .replaceAll("12th", "twelfth")
        .replaceAll("13th", "thirteenth")
        .replaceAll("14th", "fourteenth")
        .replaceAll("15th", "fifteenth")
        .replaceAll("16th", "sixteenth")
        .replaceAll("17th", "seventeenth")
        .replaceAll("18th", "eighteenth")
        .replaceAll("19th", "nineteenth")

        .replaceAll("20th", "twenteeth")
        .replaceAll("21th", "twenty-first")
        .replaceAll("22th", "twenty-second")
        .replaceAll("23th", "twenty-third")
        .replaceAll("24th", "twenty-fourth")
        .replaceAll("25th", "twenty-fifth")
        .replaceAll("26th", "twenty-sixth")
        .replaceAll("27th", "twenty-seventh")
        .replaceAll("28th", "twenty-eigth")
        .replaceAll("29th", "twenty-ninth")

        .replaceAll("30th", "thirteeth")
        .replaceAll("31th", "thirty-first")
        .replaceAll("32th", "thirty-second")
        .replaceAll("33th", "thirty-third")
        .replaceAll("34th", "thirty-fourth")
        .replaceAll("35th", "thirty-fifth")
        .replaceAll("36th", "thirty-sixth")
        .replaceAll("37th", "thirty-seventh")
        .replaceAll("38th", "thirty-eigth")
        .replaceAll("39th", "thirty-ninth")
    } else if (usState.id == "NH" && divisionScope == divisionScopeHouse) {
      districtFromWS.replaceAll("^New\\sHampshire\\sState\\sHouse\\sdistrict\\s(\\w+)\\sCounty\\sNo\\.\\s(\\d+)$", "$1 $2")
    } else if (usState.id == "SC" && divisionScope == divisionScopeHouse) {
      districtFromWS.replaceAll("^South\\sCarolina\\sState\\sHouse\\sdistrict\\sHD-(\\d+)$", "$1")
      .toInt  // To get rid of the heading zero
      .toString
    } else if (usState.id == "VT") {
      districtFromWS.replaceAll("^Vermont\\s((\\w|-)+)\\sState\\s(House|Senate)\\sdistrict$", "$1")
    } else {
      val lastWord = districtFromWS.split("\\s").last

      val castDistrict = try {
        lastWord.toInt
      }
      catch {
        case e: NumberFormatException => lastWord
      }

      castDistrict.toString
    }
  }
}
