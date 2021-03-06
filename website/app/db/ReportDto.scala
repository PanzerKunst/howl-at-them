package db

import anorm._
import play.api.Logger
import models.{SupportLevel, Report}
import play.api.db.DB
import play.api.Play.current
import java.util.Date

object ReportDto {
  def create(report: Report): Option[Long] = {
    DB.withConnection {
      implicit c =>

        var contactForQuery = "NULL"
        if (report.contact.isDefined && report.contact.get != "")
          contactForQuery = "'" + DbUtil.safetize(report.contact.get) + "'"

        var supportLevelForQuery = "NULL"
        if (report.supportLevel != SupportLevel.UNKNOWN.toString)
          supportLevelForQuery = "'" + DbUtil.safetize(report.supportLevel) + "'"

        val query = """
               insert into report(candidate_id, author_name, contact, is_money_in_politics_a_problem, is_supporting_amendment_to_fix_it,
          is_opposing_citizens_united, is_supporting_convention_process, support_level, notes, creation_timestamp)
          values(""" + report.candidateId + """, '""" +
          DbUtil.safetize(report.authorName) + """', """ +
          contactForQuery + """, """ +
          report.isMoneyInPoliticsAProblem.getOrElse("NULL") + """, """ +
          report.isSupportingAmendmentToFixIt.getOrElse("NULL") + """, """ +
          report.isOpposingCitizensUnited.getOrElse("NULL") + """, """ +
          report.isSupportingConventionProcess.getOrElse("NULL") + """, """ +
          supportLevelForQuery + """,
          {notes}, """ +
          new Date().getTime + """);"""

        Logger.info("ReportDto.create():" + query)

        // We use "on" because it's useful to handle carriage returns
        SQL(query).on(
          "notes" -> report.notes
        ).executeInsert()
    }
  }

  def getOfId(id: Long): Option[Report] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select candidate_id, author_name, contact, is_money_in_politics_a_problem, is_supporting_amendment_to_fix_it,
            is_opposing_citizens_united, is_supporting_convention_process, support_level, notes,
            creation_timestamp, is_deleted
          from report
          where id = """ + id + """;"""

        Logger.info("ReportDto.getOfId(" + id + "):" + query)

        SQL(query).apply().headOption match {
          case Some(row) =>
            Some(
              Report(
                Some(id),
                row[Int]("candidate_id"),
                row[String]("author_name"),
                row[Option[String]]("contact"),
                row[Option[Boolean]]("is_money_in_politics_a_problem"),
                row[Option[Boolean]]("is_supporting_amendment_to_fix_it"),
                row[Option[Boolean]]("is_opposing_citizens_united"),
                row[Option[Boolean]]("is_supporting_convention_process"),
                row[Option[String]]("support_level").getOrElse(SupportLevel.UNKNOWN.toString),
                row[Option[String]]("notes"),
                row[Option[Long]]("creation_timestamp"),
                row[Boolean]("is_deleted")
              )
            )
          case None => None
        }
    }
  }

  def getOfCandidate(candidateId: Int): List[Report] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select id, author_name, contact, is_money_in_politics_a_problem, is_supporting_amendment_to_fix_it,
            is_opposing_citizens_united, is_supporting_convention_process, support_level, notes,
            creation_timestamp
          from report
          where candidate_id = """ + candidateId + """
            and is_deleted is false
          order by creation_timestamp desc;"""

        Logger.info("ReportDto.getOfCandidate():" + query)

        SQL(query)().map {
          row =>
            Report(row[Option[Long]]("id"),
              candidateId,
              row[String]("author_name"),
              row[Option[String]]("contact"),
              row[Option[Boolean]]("is_money_in_politics_a_problem"),
              row[Option[Boolean]]("is_supporting_amendment_to_fix_it"),
              row[Option[Boolean]]("is_opposing_citizens_united"),
              row[Option[Boolean]]("is_supporting_convention_process"),
              row[Option[String]]("support_level").getOrElse(SupportLevel.UNKNOWN.toString),
              row[Option[String]]("notes"),
              row[Option[Long]]("creation_timestamp"))
        }.toList
    }
  }

  def update(report: Report) {
    DB.withConnection {
      implicit c =>

        var contactForQuery = "NULL"
        if (report.contact.isDefined && report.contact.get != "")
          contactForQuery = "'" + DbUtil.safetize(report.contact.get) + "'"

        var supportLevelForQuery = "NULL"
        if (report.supportLevel != SupportLevel.UNKNOWN.toString)
          supportLevelForQuery = "'" + DbUtil.safetize(report.supportLevel) + "'"

        val query = """
          update report set
          author_name = '""" + DbUtil.safetize(report.authorName) + """',
          contact = """ + contactForQuery + """,
          is_money_in_politics_a_problem = """ + report.isMoneyInPoliticsAProblem.getOrElse("NULL") + """,
          is_supporting_amendment_to_fix_it = """ + report.isSupportingAmendmentToFixIt.getOrElse("NULL") + """,
          is_opposing_citizens_united = """ + report.isOpposingCitizensUnited.getOrElse("NULL") + """,
          is_supporting_convention_process = """ + report.isSupportingConventionProcess.getOrElse("NULL") + """,
          support_level = """ + supportLevelForQuery + """,
          notes = {notes}
          where id = """ + report.id.get + """;"""

        Logger.info("ReportDto.update():" + query)

        // We use "on" because it's useful to handle carriage returns
        SQL(query).on(
          "notes" -> report.notes
        ).executeUpdate()
    }
  }

  def delete(report: Report) {
    DB.withConnection {
      implicit c =>

        val query = """
          update report set
          is_deleted = true
          where id = """ + report.id.get + """;"""

        Logger.info("ReportDto.delete():" + query)

        SQL(query).executeUpdate()
    }
  }
}
