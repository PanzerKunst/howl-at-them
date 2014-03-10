package models.frontend

import play.api.libs.json.{Json, JsValue, Writes}
import models.{Report, StateLegislator}

case class StateLegislatorWithLatestReportWithNbReports(stateLegislator: StateLegislator,
                                                        latestReport: Option[Report],
                                                        nbReports: Int)

object StateLegislatorWithLatestReportWithNbReports {
  implicit val writes = new Writes[StateLegislatorWithLatestReportWithNbReports] {
    def writes(stateLegislatorWithLatestReportWithNbReports: StateLegislatorWithLatestReportWithNbReports): JsValue = {
      Json.obj(
        "stateLegislator" -> stateLegislatorWithLatestReportWithNbReports.stateLegislator,
        "latestReport" -> stateLegislatorWithLatestReportWithNbReports.latestReport,
        "nbReports" -> stateLegislatorWithLatestReportWithNbReports.nbReports
      )
    }
  }
}
