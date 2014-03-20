package db

import anorm._
import play.api.db.DB
import play.api.Logger
import models._
import play.api.Play.current
import frontend.DetailedStateLegislator
import scala.Some

object StateLegislatorDto {
  def getOfId(id: Int): Option[DetailedStateLegislator] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select first_name, last_name, title, political_parties, us_state_id, district,
            leadership_position,
            office_type, office_street, office_city, office_zip, office_us_state_id, office_phone_number,
            committee_id, committee_name,
            s.name,
            r.id as report_id, r.author_name, r.support_level, r.is_money_in_politics_a_problem, r.is_supporting_amendment_to_fix_it,
            r.is_opposing_citizens_united, r.has_previously_voted_for_convention, r.contact, r.creation_timestamp,
            r.notes
          from state_legislator l
          inner join us_state s
            on s.id = l.us_state_id
          left join report r
            on r.candidate_id = l.id
            and r.is_deleted is false
          where l.id = """ + id + """
          order by creation_timestamp desc;"""

        Logger.info("StateLegislatorDto.getOfId(" + id + "):" + query)

        val denormalizedStateLegislator = SQL(query)().map {
          row =>
            var politicalParties: List[String] = List()

            row[Option[String]]("political_parties") match {
              case Some(commaSeparatedPoliticalParties) =>
                val politicalPartiesSplit = commaSeparatedPoliticalParties.split(",")
                for (untrimmedPoliticalParty <- politicalPartiesSplit) {
                  politicalParties = politicalParties :+ untrimmedPoliticalParty.trim()
                }
              case None =>
            }

            val stateLegislator = new StateLegislator(id,
              row[String]("first_name"),
              row[String]("last_name"),
              row[String]("title"),
              politicalParties,
              UsState(row[String]("us_state_id"), row[String]("name")),
              row[String]("district"),
              row[Option[String]]("leadership_position"))

            val candidateOffice = row[Option[String]]("office_type") match {
              case Some(officeType) => Some(CandidateOffice(id,
                officeType,
                row[Option[String]]("office_street"),
                row[Option[String]]("office_city"),
                row[Option[String]]("office_us_state_id"),
                row[Option[String]]("office_zip"),
                row[Option[String]]("office_phone_number")))
              case None => None
            }

            val candidateCommittee = CandidateCommittee(id,
              row[Option[Int]]("committee_id"),
              row[Option[String]]("committee_name"))

            val report = row[Option[Long]]("report_id") match {
              case Some(reportId) => Some(Report(
                Some(reportId),
                id,
                row[String]("author_name"),
                row[String]("contact"),
                row[Option[Boolean]]("is_money_in_politics_a_problem"),
                row[Option[Boolean]]("is_supporting_amendment_to_fix_it"),
                row[Option[Boolean]]("is_opposing_citizens_united"),
                row[Option[Boolean]]("has_previously_voted_for_convention"),
                row[Option[String]]("support_level"),
                row[Option[String]]("notes"),
                row[Option[Long]]("creation_timestamp")
              ))
              case None => None
            }

            (stateLegislator, candidateOffice, candidateCommittee, report)
        }

        normalizeStateLegislator(denormalizedStateLegislator)
    }
  }

  def getMatching(firstNameFilter: Option[String], lastNameFilter: Option[String], usStateId: Option[String]): List[DetailedStateLegislator] = {
    DB.withConnection {
      implicit c =>

        val firstNameFilterForQuery = firstNameFilter match {
          case Some(fnFilter) => DbUtil.safetize(fnFilter.replaceAll("\\*", "%"))
          case None => "%"
        }

        val lastNameFilterForQuery = lastNameFilter match {
          case Some(lnFilter) => DbUtil.safetize(lnFilter.replaceAll("\\*", "%"))
          case None => "%"
        }

        val usStateIdFilterForQuery = usStateId match {
          case Some(stateId) => DbUtil.safetize(stateId)
          case None => "%"
        }

        val query = """
          select distinct l.id, first_name, last_name, title, political_parties, us_state_id, district,
            leadership_position,
            s.name,
            r.id as report_id, r.author_name, r.support_level, r.is_money_in_politics_a_problem, r.is_supporting_amendment_to_fix_it,
            r.is_opposing_citizens_united, r.has_previously_voted_for_convention, r.contact, r.creation_timestamp,
            r.notes
          from state_legislator l
          inner join us_state s
            on s.id = l.us_state_id
          left join report r
            on r.candidate_id = l.id
            and r.is_deleted is false
          where lower(first_name) like '""" + firstNameFilterForQuery + """'
            and lower(last_name) like '""" + lastNameFilterForQuery + """'
            and us_state_id like '""" + usStateIdFilterForQuery + """'
          order by title, first_name, creation_timestamp desc;"""

        Logger.info("StateLegislatorDto.getMatching():" + query)

        val denormalizedStateLegislatorsWithReports = SQL(query)().map {
          row =>
            var politicalParties: List[String] = List()

            row[Option[String]]("political_parties") match {
              case Some(commaSeparatedPoliticalParties) =>
                val politicalPartiesSplit = commaSeparatedPoliticalParties.split(",")
                for (untrimmedPoliticalParty <- politicalPartiesSplit) {
                  politicalParties = politicalParties :+ untrimmedPoliticalParty.trim()
                }
              case None =>
            }

            val candidateId = row[Int]("id")

            val stateLegislator = new StateLegislator(candidateId,
              row[String]("first_name"),
              row[String]("last_name"),
              row[String]("title"),
              politicalParties,
              UsState(row[String]("us_state_id"), row[String]("name")),
              row[String]("district"),
              row[Option[String]]("leadership_position"))

            val report = row[Option[Long]]("report_id") match {
              case Some(reportId) => Some(Report(
                Some(reportId),
                candidateId,
                row[String]("author_name"),
                row[String]("contact"),
                row[Option[Boolean]]("is_money_in_politics_a_problem"),
                row[Option[Boolean]]("is_supporting_amendment_to_fix_it"),
                row[Option[Boolean]]("is_opposing_citizens_united"),
                row[Option[Boolean]]("has_previously_voted_for_convention"),
                row[Option[String]]("support_level"),
                row[Option[String]]("notes"),
                row[Option[Long]]("creation_timestamp")
              ))
              case None => None
            }

            (stateLegislator, report)
        }

        normalizeStateLegislatorsWithReports(denormalizedStateLegislatorsWithReports)
    }
  }

  def getOfStateId(usStateId: String): List[DetailedStateLegislator] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select distinct l.id, first_name, last_name, title, political_parties, district,
            leadership_position,
            s.name,
            r.id as report_id, r.author_name, r.support_level, r.is_money_in_politics_a_problem, r.is_supporting_amendment_to_fix_it,
            r.is_opposing_citizens_united, r.has_previously_voted_for_convention, r.contact, r.creation_timestamp,
            r.notes
          from state_legislator l
          inner join us_state s
            on s.id = l.us_state_id
          left join report r
            on r.candidate_id = l.id
            and r.is_deleted is false
          where us_state_id = '""" + DbUtil.safetize(usStateId) + """'
          order by title, first_name, creation_timestamp desc;"""

        Logger.info("StateLegislatorDto.getOfStateId():" + query)

        val denormalizedStateLegislatorsWithReports = SQL(query)().map {
          row =>
            var politicalParties: List[String] = List()

            row[Option[String]]("political_parties") match {
              case Some(commaSeparatedPoliticalParties) =>
                val politicalPartiesSplit = commaSeparatedPoliticalParties.split(",")
                for (untrimmedPoliticalParty <- politicalPartiesSplit) {
                  politicalParties = politicalParties :+ untrimmedPoliticalParty.trim()
                }
              case None =>
            }

            val candidateId = row[Int]("id")

            val stateLegislator = new StateLegislator(candidateId,
              row[String]("first_name"),
              row[String]("last_name"),
              row[String]("title"),
              politicalParties,
              UsState(usStateId, row[String]("name")),
              row[String]("district"),
              row[Option[String]]("leadership_position"))

            val report = row[Option[Long]]("report_id") match {
              case Some(reportId) => Some(Report(
                Some(reportId),
                candidateId,
                row[String]("author_name"),
                row[String]("contact"),
                row[Option[Boolean]]("is_money_in_politics_a_problem"),
                row[Option[Boolean]]("is_supporting_amendment_to_fix_it"),
                row[Option[Boolean]]("is_opposing_citizens_united"),
                row[Option[Boolean]]("has_previously_voted_for_convention"),
                row[Option[String]]("support_level"),
                row[Option[String]]("notes"),
                row[Option[Long]]("creation_timestamp")
              ))
              case None => None
            }

            (stateLegislator, report)
        }

        normalizeStateLegislatorsWithReports(denormalizedStateLegislatorsWithReports)
    }
  }

  def getOfDistricts(statesAndDistricts: List[StateAndDistrict]): List[DetailedStateLegislator] = {
    DB.withConnection {
      implicit c =>

        val stateIdsForQuery = statesAndDistricts.map {
          stateAndDistrict => "'%s'".format(DbUtil.safetize(stateAndDistrict.usState.id))
        }
          .mkString(", ")

        val districtsForQuery = statesAndDistricts.map {
          stateAndDistrict => "'%s'".format(DbUtil.safetize(stateAndDistrict.district))
        }
          .mkString(", ")

        val query = """
          select distinct l.id, first_name, last_name, title, political_parties, us_state_id, district,
            leadership_position,
            s.name,
            r.id as report_id, r.author_name, r.support_level, r.is_money_in_politics_a_problem, r.is_supporting_amendment_to_fix_it,
            r.is_opposing_citizens_united, r.has_previously_voted_for_convention, r.contact, r.creation_timestamp,
            r.notes
          from state_legislator l
          inner join us_state s
            on s.id = l.us_state_id
          left join report r
            on r.candidate_id = l.id
            and r.is_deleted is false
          where us_state_id in (""" + stateIdsForQuery + """)
            and district in (""" + districtsForQuery + """)
          order by title, first_name, creation_timestamp desc;"""

        Logger.info("StateLegislatorDto.getOfDistrict():" + query)

        val denormalizedStateLegislatorsWithReports = SQL(query)().map {
          row =>
            var politicalParties: List[String] = List()

            row[Option[String]]("political_parties") match {
              case Some(commaSeparatedPoliticalParties) =>
                val politicalPartiesSplit = commaSeparatedPoliticalParties.split(",")
                for (untrimmedPoliticalParty <- politicalPartiesSplit) {
                  politicalParties = politicalParties :+ untrimmedPoliticalParty.trim()
                }
              case None =>
            }

            val candidateId = row[Int]("id")

            val stateLegislator = new StateLegislator(candidateId,
              row[String]("first_name"),
              row[String]("last_name"),
              row[String]("title"),
              politicalParties,
              UsState(row[String]("us_state_id"), row[String]("name")),
              row[String]("district"),
              row[Option[String]]("leadership_position"))

            val report = row[Option[Long]]("report_id") match {
              case Some(reportId) => Some(Report(
                Some(reportId),
                candidateId,
                row[String]("author_name"),
                row[String]("contact"),
                row[Option[Boolean]]("is_money_in_politics_a_problem"),
                row[Option[Boolean]]("is_supporting_amendment_to_fix_it"),
                row[Option[Boolean]]("is_opposing_citizens_united"),
                row[Option[Boolean]]("has_previously_voted_for_convention"),
                row[Option[String]]("support_level"),
                row[Option[String]]("notes"),
                row[Option[Long]]("creation_timestamp")
              ))
              case None => None
            }

            (stateLegislator, report)
        }

        normalizeStateLegislatorsWithReports(denormalizedStateLegislatorsWithReports)
    }
  }

  private def normalizeStateLegislator(denormalizedStateLegislator: Stream[(StateLegislator, Option[CandidateOffice], CandidateCommittee, Option[Report])]): Option[DetailedStateLegislator] = {
    if (denormalizedStateLegislator.isEmpty)
      None
    else {
      val stateLegislator = denormalizedStateLegislator.head._1

      var candidateOffices: List[CandidateOffice] = List()
      var candidateCommittees: List[CandidateCommittee] = List()
      var reports: List[Report] = List()

      for (row <- denormalizedStateLegislator) {
        val candidateCommittee = row._3
        val reportOption = row._4

        var isInListAlready = false


        // candidateOffice

        row._2 match {
          case Some(candidateOffice) =>
            for (candidateOfficeItem <- candidateOffices) {
              if (!isInListAlready && candidateOfficeItem.officeType == candidateOffice.officeType) {
                isInListAlready = true
              }
            }

            if (!isInListAlready) {
              candidateOffices = candidateOffices :+ candidateOffice
            }
          case None =>
        }


        // candidateCommittee

        if (candidateCommittee.committeeId.isDefined) {
          isInListAlready = false

          for (candidateCommitteeItem <- candidateCommittees) {
            if (!isInListAlready && candidateCommitteeItem.committeeId.get == candidateCommittee.committeeId.get) {
              isInListAlready = true
            }
          }

          if (!isInListAlready) {
            candidateCommittees = candidateCommittees :+ candidateCommittee
          }
        }


        // reports

        reportOption match {
          case Some(report) =>
            isInListAlready = false

            for (reportItem <- reports) {
              if (!isInListAlready && reportItem.id == report.id) {
                isInListAlready = true
              }
            }

            if (!isInListAlready) {
              reports = reports :+ report
            }
          case None =>
        }
      }

      Some(
        new DetailedStateLegislator(stateLegislator.id,
          stateLegislator.firstName,
          stateLegislator.lastName,
          stateLegislator.title,
          stateLegislator.politicalParties,
          stateLegislator.usState,
          stateLegislator.district,
          stateLegislator.leadershipPosition,

          candidateOffices,
          candidateCommittees,
          false,
          reports)
      )
    }
  }

  private def normalizeStateLegislatorsWithReports(denormalizedStateLegislatorsWithReports: Stream[(StateLegislator, Option[Report])]): List[DetailedStateLegislator] = {
    var result: List[DetailedStateLegislator] = List()

    for ((stateLegislator, reportOption) <- denormalizedStateLegislatorsWithReports) {
      reportOption match {
        case None =>
          result = result :+ new DetailedStateLegislator(stateLegislator.id,
            stateLegislator.firstName,
            stateLegislator.lastName,
            stateLegislator.title,
            stateLegislator.politicalParties,
            stateLegislator.usState,
            stateLegislator.district)

        case Some(report) =>
          var isInListAlready = false

          for (detailedStateLegislatorItem <- result) {
            if (!isInListAlready && detailedStateLegislatorItem.id == report.candidateId) {
              // If the legislator is already in the results, we update its reports
              isInListAlready = true
              detailedStateLegislatorItem.reports = detailedStateLegislatorItem.reports :+ report
            }
          }

          // Otherwise, we add the legislator to the results, with this report
          if (!isInListAlready) {
            result = result :+ new DetailedStateLegislator(stateLegislator.id,
              stateLegislator.firstName,
              stateLegislator.lastName,
              stateLegislator.title,
              stateLegislator.politicalParties,
              stateLegislator.usState,
              stateLegislator.district,
              stateLegislator.leadershipPosition,

              List(),
              List(),
              false,
              List(report))
          }
      }
    }

    result
  }
}
