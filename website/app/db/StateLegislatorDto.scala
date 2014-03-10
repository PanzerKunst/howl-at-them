package db

import anorm._
import play.api.db.DB
import play.api.Logger
import models._
import play.api.Play.current
import frontend.{StateLegislatorWithLatestReportWithNbReports, DetailedStateLegislator}
import scala.Some

object StateLegislatorDto {
  def getOfId(id: Int): Option[DetailedStateLegislator] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select first_name, last_name, title, political_parties, us_state_id, district,
            office_type, office_street, office_city, office_zip, office_us_state_id, office_phone_number,
            committee_id, committee_name,
            s.name
          from state_legislator l
          inner join us_state s on s.id = l.us_state_id
          where l.id = """ + id + """;"""

        Logger.info("StateLegislatorDto.getOfId(" + id + "):" + query)

        val denormalizedStateLegislator = SQL(query)().map { row =>
          var politicalParties: List[String] = List()

          row[Option[String]]("political_parties") match {
            case Some(commaSeparatedPoliticalParties) =>
              val politicalPartiesSplit = commaSeparatedPoliticalParties.split(",")
              for (untrimmedPoliticalParty <- politicalPartiesSplit) {
                politicalParties = politicalParties :+ untrimmedPoliticalParty.trim()
              }
            case None =>
          }

          val detailedStateLegislator = new StateLegislator(id,
            row[String]("first_name"),
            row[String]("last_name"),
            row[String]("title"),
            politicalParties,
            UsState(row[String]("us_state_id"), row[String]("name")),
            row[String]("district"))

          val candidateOffice = new CandidateOffice(id,
            row[String]("office_type"),
            row[Option[String]]("office_street"),
            row[Option[String]]("office_city"),
            row[Option[String]]("office_us_state_id"),
            row[Option[String]]("office_zip"),
            row[Option[String]]("office_phone_number"))

          val candidateCommittee = new CandidateCommittee(id,
            row[Option[Int]]("committee_id"),
            row[Option[String]]("committee_name"))

          (detailedStateLegislator, candidateOffice, candidateCommittee)
        }

        normalizeStateLegislator(denormalizedStateLegislator)
    }
  }

  def getMatching(firstNameFilter: Option[String], lastNameFilter: Option[String], usStateId: Option[String]): List[StateLegislatorWithLatestReportWithNbReports] = {
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
            s.name,
            r.id as report_id, r.author_name, r.support_level, r.is_money_in_politics_a_problem, r.is_supporting_amendment_to_fix_it,
            r.is_opposing_citizens_united, r.has_previously_voted_for_convention, r.contact, r.creation_timestamp,
            r.notes,
            rc.nb_reports
          from state_legislator l
          inner join us_state s
            on s.id = l.us_state_id
          left join (select id, candidate_id, author_name, support_level, is_money_in_politics_a_problem, is_supporting_amendment_to_fix_it, is_opposing_citizens_united, has_previously_voted_for_convention, contact, creation_timestamp, notes
            from report r1
            where creation_timestamp = (
              select max(creation_timestamp) from report r2
              where r2.candidate_id = r1.candidate_id
              group by candidate_id)
            ) r
            on r.candidate_id = l.id
          left join (select count(id) as nb_reports, candidate_id
            from report
            group by candidate_id) rc
            on rc.candidate_id = l.id
          where lower(first_name) like '""" + firstNameFilterForQuery + """'
            and lower(last_name) like '""" + lastNameFilterForQuery + """'
            and us_state_id like '""" + usStateIdFilterForQuery + """'
          order by last_name;"""

        Logger.info("StateLegislatorDto.getMatching():" + query)

        SQL(query)().map { row =>
          var politicalParties: List[String] = List()

          row[Option[String]]("political_parties") match {
            case Some(commaSeparatedPoliticalParties) =>
              val politicalPartiesSplit = commaSeparatedPoliticalParties.split(",")
              for (untrimmedPoliticalParty <- politicalPartiesSplit) {
                politicalParties = politicalParties :+ untrimmedPoliticalParty.trim()
              }
            case None =>
          }

          val stateLegislator = new StateLegislator(
            row[Int]("id"),
            row[String]("first_name"),
            row[String]("last_name"),
            row[String]("title"),
            politicalParties,
            UsState(row[String]("us_state_id"), row[String]("name")),
            row[String]("district")
          )

          val latestReport = row[Option[String]]("author_name") match {
            case Some(authorName) => Some(
              Report(
                row[Option[Long]]("report_id"),
                row[Int]("id"),
                authorName,
                row[String]("contact"),
                row[Option[Boolean]]("is_money_in_politics_a_problem"),
                row[Option[Boolean]]("is_supporting_amendment_to_fix_it"),
                row[Option[Boolean]]("is_opposing_citizens_united"),
                row[Option[Boolean]]("has_previously_voted_for_convention"),
                row[Option[String]]("support_level"),
                row[String]("notes"),
                row[Option[Long]]("creation_timestamp")
              )
            )
            case None => None
          }

          val nbReports = row[Option[Long]]("nb_reports") match {
            case Some(nb) => nb.toInt
            case None => 0
          }

          StateLegislatorWithLatestReportWithNbReports(stateLegislator, latestReport, nbReports)
        }.toList
    }
  }

  private def normalizeStateLegislator(denormalizedStateLegislator: Stream[(StateLegislator, CandidateOffice, CandidateCommittee)]): Option[DetailedStateLegislator] = {
    if (denormalizedStateLegislator.isEmpty)
      None
    else {
      val stateLegislator = denormalizedStateLegislator.head._1

      var candidateOffices: List[CandidateOffice] = List()

      for (row <- denormalizedStateLegislator) {
        val candidateOffice = row._2

        var isInListAlready = false

        for (candidateOfficeItem <- candidateOffices) {
          if (candidateOfficeItem.officeType == candidateOffice.officeType) {
            isInListAlready = true
          }
        }

        if (!isInListAlready) {
          candidateOffices = candidateOffices :+ candidateOffice
        }
      }


      var candidateCommittees: List[CandidateCommittee] = List()

      for (row <- denormalizedStateLegislator) {
        val candidateCommittee = row._3

        if (candidateCommittee.committeeId.isDefined) {
          var isInListAlready = false

          for (candidateCommitteeItem <- candidateCommittees) {
            if (candidateCommitteeItem.committeeId.get == candidateCommittee.committeeId.get) {
              isInListAlready = true
            }
          }

          if (!isInListAlready) {
            candidateCommittees = candidateCommittees :+ candidateCommittee
          }
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

          candidateOffices,
          candidateCommittees)
      )
    }
  }
}
