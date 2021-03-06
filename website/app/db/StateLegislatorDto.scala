package db

import java.util.Date

import anorm._
import play.api.db.DB
import play.api.Logger
import models._
import play.api.Play.current
import frontend.DetailedStateLegislator
import scala.Some
import anorm.SqlParser._
import models.votesmart.VoteSmartCandidateWebAddress

object StateLegislatorDto {
  def getOfId(id: Int): Option[DetailedStateLegislator] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select first_name, last_name, title, political_parties, us_state_id, district,
            leadership_position_id, leadership_position_name,
            office_type, office_street, office_city, office_zip, office_us_state_id, office_phone_number,
            web_address_type_id, web_address_type, web_address,
            committee_membership_id, committee_position,
            committee_id, committee_us_state_id, committee_name,
            other_phone_number, is_a_priority_target, is_missing_urgent_report, staff_name, staff_number, point_of_contact,
            s.name,
            r.id as report_id, r.author_name, r.support_level, r.is_money_in_politics_a_problem, r.is_supporting_amendment_to_fix_it,
            r.is_opposing_citizens_united, r.is_supporting_convention_process, r.contact, r.creation_timestamp,
            r.notes
          from state_legislator l
          inner join us_state s
            on s.id = l.us_state_id
          left join report r
            on r.candidate_id = l.id
            and r.is_deleted is false
          where l.id = """ + id + """
          order by creation_timestamp desc;"""

        Logger.info("StateLegislatorDto.getOfId():" + query)

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

            val leadershipPosition = row[Option[Int]]("leadership_position_id") match {
              case Some(leadershipPositionId) => Some(LeadershipPosition(leadershipPositionId,
                row[String]("leadership_position_name")))
              case None => None
            }

            val stateLegislator = new StateLegislator(id,
              row[String]("first_name"),
              row[String]("last_name"),
              row[String]("title"),
              politicalParties,
              UsState(row[String]("us_state_id"), row[String]("name")),
              row[String]("district"),
              leadershipPosition,
              row[Option[String]]("other_phone_number"),
              row[Option[Boolean]]("is_a_priority_target").getOrElse(false),
              row[Option[Boolean]]("is_missing_urgent_report").getOrElse(false),
              row[Option[String]]("staff_name"),
              row[Option[String]]("staff_number"),
              row[Option[String]]("point_of_contact"))

            val candidateOffice = row[Option[String]]("office_type") match {
              case Some(officeType) => Some(CandidateOffice(officeType,
                row[Option[String]]("office_street"),
                row[Option[String]]("office_city"),
                row[Option[String]]("office_us_state_id"),
                row[Option[String]]("office_zip"),
                row[Option[String]]("office_phone_number")))
              case None => None
            }

            val candidateCommittee = row[Option[Int]]("committee_id") match {
              case Some(committeeId) => Some(CandidateCommittee(
                row[Long]("committee_membership_id"),
                Committee(row[Int]("committee_id"),
                  UsState(row[String]("committee_us_state_id"), row[String]("name")),
                  row[String]("committee_name")
                ),
                row[String]("committee_position")
              ))
              case None => None
            }

            val report = row[Option[Long]]("report_id") match {
              case Some(reportId) => Some(Report(
                Some(reportId),
                id,
                row[String]("author_name"),
                row[Option[String]]("contact"),
                row[Option[Boolean]]("is_money_in_politics_a_problem"),
                row[Option[Boolean]]("is_supporting_amendment_to_fix_it"),
                row[Option[Boolean]]("is_opposing_citizens_united"),
                row[Option[Boolean]]("is_supporting_convention_process"),
                row[Option[String]]("support_level").getOrElse(SupportLevel.UNKNOWN.toString),
                row[Option[String]]("notes"),
                row[Option[Long]]("creation_timestamp")
              ))
              case None => None
            }

            val webAddressTypeId = row[Option[Int]]("web_address_type_id")

            val emailAddress = if (webAddressTypeId.isDefined && webAddressTypeId.get == VoteSmartCandidateWebAddress.emailTypeId) {
              Some(row[String]("web_address"))
            } else {
              None
            }

            val websiteUrl = if (webAddressTypeId.isDefined && webAddressTypeId.get == VoteSmartCandidateWebAddress.websiteTypeId) {
              Some(row[String]("web_address"))
            } else {
              None
            }

            val facebookUrl = if (webAddressTypeId.isDefined && webAddressTypeId.get == VoteSmartCandidateWebAddress.facebookTypeId) {
              Some(row[String]("web_address"))
            } else {
              None
            }

            val twitterUrl = if (webAddressTypeId.isDefined && webAddressTypeId.get == VoteSmartCandidateWebAddress.twitterTypeId) {
              Some(row[String]("web_address"))
            } else {
              None
            }

            (stateLegislator, candidateOffice, candidateCommittee, report, emailAddress, websiteUrl, facebookUrl, twitterUrl)
        }

        normalizeStateLegislator(denormalizedStateLegislator)
    }
  }

  def doesStateLegislatorHaveExtraInfo(stateLegislator: StateLegislator): Boolean = {
    DB.withConnection {
      implicit c =>

        val query = """
          select count(*) from state_legislator_extra
          where candidate_id = """ + stateLegislator.id + """;"""

        Logger.info("StateLegislatorDto.doesStateLegislatorHaveExtraInfo():" + query)

        val count: Long = SQL(query).as(scalar[Long].single)

        count > 0
    }
  }

  def insertExtras(detailedStateLegislator: DetailedStateLegislator) {
    DB.withConnection {
      implicit c =>

        var otherPhoneNumberForQuery = "NULL"
        if (detailedStateLegislator.otherPhoneNumber.isDefined && detailedStateLegislator.otherPhoneNumber.get != "")
          otherPhoneNumberForQuery = "'" + DbUtil.safetize(detailedStateLegislator.otherPhoneNumber.get) + "'"

        val query = """
               insert into state_legislator_extra(candidate_id, other_phone_number, is_a_priority_target, is_missing_urgent_report)
          values(""" + detailedStateLegislator.id + """, """ +
          otherPhoneNumberForQuery + """, """ +
          detailedStateLegislator.isAPriorityTarget + """, """ +
          detailedStateLegislator.isMissingUrgentReport + """);"""

        Logger.info("StateLegislatorDto.insertExtras():" + query)

        SQL(query).executeInsert()
    }
  }

  def updateExtras(detailedStateLegislator: DetailedStateLegislator) {
    DB.withConnection {
      implicit c =>

        var otherPhoneNumberForQuery = "NULL"
        if (detailedStateLegislator.otherPhoneNumber.isDefined && detailedStateLegislator.otherPhoneNumber.get != "")
          otherPhoneNumberForQuery = "'" + DbUtil.safetize(detailedStateLegislator.otherPhoneNumber.get) + "'"

        var staffNameForQuery = "NULL"
        if (detailedStateLegislator.staffName.isDefined && detailedStateLegislator.staffName.get != "")
          staffNameForQuery = "'" + DbUtil.safetize(detailedStateLegislator.staffName.get) + "'"

        var staffNumberForQuery = "NULL"
        if (detailedStateLegislator.staffNumber.isDefined && detailedStateLegislator.staffNumber.get != "")
          staffNumberForQuery = "'" + DbUtil.safetize(detailedStateLegislator.staffNumber.get) + "'"

        var pointOfContactForQuery = "NULL"
        if (detailedStateLegislator.pointOfContact.isDefined && detailedStateLegislator.pointOfContact.get != "")
          pointOfContactForQuery = "'" + DbUtil.safetize(detailedStateLegislator.pointOfContact.get) + "'"

        val query = """
          update state_legislator_extra set
            other_phone_number = """ + otherPhoneNumberForQuery + """,
            is_a_priority_target = """ + detailedStateLegislator.isAPriorityTarget + """,
            is_missing_urgent_report = """ + detailedStateLegislator.isMissingUrgentReport + """,
            staff_name = """ + staffNameForQuery + """,
            staff_number = """ + staffNumberForQuery + """,
            point_of_contact = """ + pointOfContactForQuery + """
          where candidate_id = """ + detailedStateLegislator.id + """;"""

        Logger.info("StateLegislatorDto.updateExtras():" + query)

        SQL(query).executeUpdate()
    }
  }

  def getMatching(usStateId: String, nbDaysSinceLastReport: Option[Int], nbDaysWithoutReport: Option[Int], chamberAbbrOrPriorityTarget: Option[String], leadershipPositionId: Option[Int], committees: List[Committee]): List[DetailedStateLegislator] = {
    val nbDaysSinceLastReportClause = nbDaysSinceLastReport match {
      case Some(nbDays) =>
        val minTimestamp = new Date().getTime - nbDays * 24 * 3600 * 1000

        """
          and max_creation_timestamp > """ + minTimestamp
      case None => ""
    }

    val nbDaysWithoutReportClause = nbDaysWithoutReport match {
      case Some(nbDays) =>
        val maxTimestamp = new Date().getTime - nbDays * 24 * 3600 * 1000

        """
          and (max_creation_timestamp <= """ + maxTimestamp + """ or
            max_creation_timestamp is null)"""
      case None => ""
    }

    val (titleClause, priorityTargetClause) = chamberAbbrOrPriorityTarget match {
      case Some(chamberOrTarget) =>
        if (chamberOrTarget == Chamber.HOUSE.getAbbr) {
          ("""
          and title in ('Representative', 'Assembly Member')""",
            "")
        } else if (chamberOrTarget == Chamber.SENATE.getAbbr) {
          ("""
          and title = 'Senator'""",
            "")
        } else if (chamberOrTarget == "priorityTarget") {
          ("",
            """
          and is_a_priority_target = true""")
        }

      case None =>
        ("", "")
    }

    val leadershipPositionIdClause = leadershipPositionId match {
      case Some(id) =>
        """
          and leadership_position_id = """ + id
      case None => ""
    }

    val committeeIdsClause = if (committees.isEmpty) {
      ""
    } else {
      val ids = committees.map {
        committee => "%s".format(committee.committeeId)
      }
        .mkString(", ")

      """
            and committee_id in (""" + ids + """)"""
    }

    val query = """
          select distinct l.id, first_name, last_name, title, political_parties, us_state_id, district,
            leadership_position_id, leadership_position_name,
            other_phone_number, is_a_priority_target, is_missing_urgent_report,
            s.name,
            r.id as report_id, r.author_name, r.support_level, r.is_money_in_politics_a_problem, r.is_supporting_amendment_to_fix_it,
            r.is_opposing_citizens_united, r.is_supporting_convention_process, r.contact, r.creation_timestamp,
            r.notes,
            lr.max_creation_timestamp
          from state_legislator l
          inner join us_state s
            on s.id = l.us_state_id
          left join report r
            on r.candidate_id = l.id
            and r.is_deleted is false
          left join (
            select candidate_id, is_deleted, max(creation_timestamp) max_creation_timestamp
            from report
            group by candidate_id, is_deleted
          ) lr
            on lr.candidate_id = l.id
            and lr.is_deleted is false
          where us_state_id = '""" + DbUtil.safetize(usStateId) + """'""" +
            nbDaysSinceLastReportClause +
            nbDaysWithoutReportClause +
            titleClause +
            priorityTargetClause +
            leadershipPositionIdClause +
            committeeIdsClause + """
          order by title, last_name, first_name, creation_timestamp desc;"""

    // Level is debug to reduce log spam
    Logger.debug("StateLegislatorDto.getMatching():" + query)

    executeFetchQueryAndReturnListOfLegislators(query)
  }

  def getOfStateId(usStateId: String): List[DetailedStateLegislator] = {
    val query = """
          select distinct l.id, first_name, last_name, title, political_parties, us_state_id, district,
            leadership_position_id, leadership_position_name,
            other_phone_number, is_a_priority_target, is_missing_urgent_report,
            s.name,
            r.id as report_id, r.author_name, r.support_level, r.is_money_in_politics_a_problem, r.is_supporting_amendment_to_fix_it,
            r.is_opposing_citizens_united, r.is_supporting_convention_process, r.contact, r.creation_timestamp,
            r.notes
          from state_legislator l
          inner join us_state s
            on s.id = l.us_state_id
          left join report r
            on r.candidate_id = l.id
            and r.is_deleted is false
          where us_state_id = '""" + DbUtil.safetize(usStateId) + """'
          order by title, last_name, first_name, creation_timestamp desc;"""

    // Level is debug to reduce log spam
    Logger.debug("StateLegislatorDto.getOfStateId():" + query)

    executeFetchQueryAndReturnListOfLegislators(query)
  }

  def getOfDistricts(statesAndDistricts: List[StateAndDistrict]): List[DetailedStateLegislator] = {
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
            leadership_position_id, leadership_position_name,
            other_phone_number, is_a_priority_target, is_missing_urgent_report,
            s.name,
            r.id as report_id, r.author_name, r.support_level, r.is_money_in_politics_a_problem, r.is_supporting_amendment_to_fix_it,
            r.is_opposing_citizens_united, r.is_supporting_convention_process, r.contact, r.creation_timestamp,
            r.notes
          from state_legislator l
          inner join us_state s
            on s.id = l.us_state_id
          left join report r
            on r.candidate_id = l.id
            and r.is_deleted is false
          where us_state_id in (""" + stateIdsForQuery + """)
            and lower(district) in (lower(""" + districtsForQuery + """))
          order by title, last_name, first_name, creation_timestamp desc;"""

    Logger.info("StateLegislatorDto.getOfDistrict():" + query)

    executeFetchQueryAndReturnListOfLegislators(query)
  }

  private def normalizeStateLegislator(denormalizedStateLegislator: Stream[(StateLegislator, Option[CandidateOffice], Option[CandidateCommittee], Option[Report], Option[String], Option[String], Option[String], Option[String])]): Option[DetailedStateLegislator] = {
    if (denormalizedStateLegislator.isEmpty)
      None
    else {
      val stateLegislator = denormalizedStateLegislator.head._1

      var candidateOffices: List[CandidateOffice] = List()
      var candidateCommittees: List[CandidateCommittee] = List()
      var reports: List[Report] = List()

      var emailAddress: Option[String] = None
      var websiteUrl: Option[String] = None
      var facebookUrl: Option[String] = None
      var twitterUrl: Option[String] = None

      for (row <- denormalizedStateLegislator) {
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
        row._3 match {
          case Some(candidateCommittee) =>
            isInListAlready = false

            for (candidateCommitteeItem <- candidateCommittees) {
              if (!isInListAlready && candidateCommitteeItem.id == candidateCommittee.id) {
                isInListAlready = true
              }
            }

            if (!isInListAlready) {
              candidateCommittees = candidateCommittees :+ candidateCommittee
            }
          case None =>
        }

        // reports
        row._4 match {
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

        // Email address
        row._5 match {
          case Some(email) => emailAddress = Some(email)
          case None =>
        }

        // Website URL
        row._6 match {
          case Some(url) => websiteUrl = Some(url)
          case None =>
        }

        // Facebook URL
        row._7 match {
          case Some(url) => facebookUrl = Some(url)
          case None =>
        }

        // Twitter URL
        row._8 match {
          case Some(url) => twitterUrl = Some(url)
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
          stateLegislator.otherPhoneNumber,
          stateLegislator.isAPriorityTarget,
          stateLegislator.isMissingUrgentReport,
          stateLegislator.staffName,
          stateLegislator.staffNumber,
          stateLegislator.pointOfContact,

          candidateOffices,
          candidateCommittees,
          reports,

          emailAddress,
          websiteUrl,
          facebookUrl,
          twitterUrl
        )
      )
    }
  }

  private def executeFetchQueryAndReturnListOfLegislators(query: String): List[DetailedStateLegislator] = {
    DB.withConnection {
      implicit c =>

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

            val leadershipPosition = row[Option[Int]]("leadership_position_id") match {
              case Some(leadershipPositionId) => Some(LeadershipPosition(leadershipPositionId,
                row[String]("leadership_position_name")))
              case None => None
            }

            val stateLegislator = new StateLegislator(candidateId,
              row[String]("first_name"),
              row[String]("last_name"),
              row[String]("title"),
              politicalParties,
              UsState(row[String]("us_state_id"), row[String]("name")),
              row[String]("district"),
              leadershipPosition,
              row[Option[String]]("other_phone_number"),
              row[Option[Boolean]]("is_a_priority_target").getOrElse(false),
              row[Option[Boolean]]("is_missing_urgent_report").getOrElse(false))

            val report = row[Option[Long]]("report_id") match {
              case Some(reportId) => Some(Report(
                Some(reportId),
                candidateId,
                row[String]("author_name"),
                row[Option[String]]("contact"),
                row[Option[Boolean]]("is_money_in_politics_a_problem"),
                row[Option[Boolean]]("is_supporting_amendment_to_fix_it"),
                row[Option[Boolean]]("is_opposing_citizens_united"),
                row[Option[Boolean]]("is_supporting_convention_process"),
                row[Option[String]]("support_level").getOrElse(SupportLevel.UNKNOWN.toString),
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
            stateLegislator.district,
            stateLegislator.leadershipPosition,
            stateLegislator.otherPhoneNumber,
            stateLegislator.isAPriorityTarget,
            stateLegislator.isMissingUrgentReport)

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
              stateLegislator.otherPhoneNumber,
              stateLegislator.isAPriorityTarget,
              stateLegislator.isMissingUrgentReport,
              None,
              None,
              None,

              List(),
              List(),

              List(report))
          }
      }
    }

    result
  }
}
