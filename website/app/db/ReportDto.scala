package db

import anorm._
import play.api.Logger
import models.Report
import play.api.db.DB
import play.api.Play.current
import java.util.Date

object ReportDto {
  def create(report: Report): Option[Long] = {
    DB.withConnection {
      implicit c =>

        var supportLevelForQuery = "NULL"
        if (report.supportLevel.isDefined && report.supportLevel.get != "")
          supportLevelForQuery = "'" + DbUtil.safetize(report.supportLevel.get) + "'"

        var notesForQuery = "NULL"
        if (report.notes.isDefined && report.notes.get != "")
          notesForQuery = "'" + DbUtil.safetize(report.notes.get) + "'"

        val query = """
               insert into report(candidate_id, author_name, contact, is_money_in_politics_a_problem, is_supporting_amendment_to_fix_it,
          is_opposing_citizens_united, has_previously_voted_for_convention, support_level, notes, creation_timestamp)
          values(""" + report.candidateId + """, '""" +
          DbUtil.safetize(report.authorName) + """', '""" +
          DbUtil.safetize(report.contact) + """', """ +
          report.isMoneyInPoliticsAProblem.getOrElse("NULL") + """, """ +
          report.isSupportingAmendmentToFixIt.getOrElse("NULL") + """, """ +
          report.isOpposingCitizensUnited.getOrElse("NULL") + """, """ +
          report.hasPreviouslyVotedForConvention.getOrElse("NULL") + """, """ +
          supportLevelForQuery + """, """ +
          notesForQuery + """, """ +
          new Date().getTime + """);"""

        Logger.info("ReportDto.create():" + query)

        SQL(query).executeInsert()
    }
  }

  def getOfId(id: Long): Option[Report] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select candidate_id, author_name, contact, is_money_in_politics_a_problem, is_supporting_amendment_to_fix_it,
            is_opposing_citizens_united, has_previously_voted_for_convention, support_level, notes,
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
                row[String]("contact"),
                row[Option[Boolean]]("is_money_in_politics_a_problem"),
                row[Option[Boolean]]("is_supporting_amendment_to_fix_it"),
                row[Option[Boolean]]("is_opposing_citizens_united"),
                row[Option[Boolean]]("has_previously_voted_for_convention"),
                row[Option[String]]("support_level"),
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
            is_opposing_citizens_united, has_previously_voted_for_convention, support_level, notes,
            creation_timestamp
          from report
          where candidate_id = """ + candidateId + """
            and is_deleted is false
          order by creation_timestamp desc;"""

        Logger.info("ReportDto.getOfCandidate():" + query)

        SQL(query)().map {
          row =>
            new Report(row[Option[Long]]("id"),
              candidateId,
              row[String]("author_name"),
              row[String]("contact"),
              row[Option[Boolean]]("is_money_in_politics_a_problem"),
              row[Option[Boolean]]("is_supporting_amendment_to_fix_it"),
              row[Option[Boolean]]("is_opposing_citizens_united"),
              row[Option[Boolean]]("has_previously_voted_for_convention"),
              row[Option[String]]("support_level"),
              row[Option[String]]("notes"),
              row[Option[Long]]("creation_timestamp"))
        }.toList
    }
  }

  def update(report: Report) {
    DB.withConnection {
      implicit c =>

        var supportLevelForQuery = "NULL"
        if (report.supportLevel.isDefined && report.supportLevel.get != "")
          supportLevelForQuery = "'" + DbUtil.safetize(report.supportLevel.get) + "'"

        var notesForQuery = "NULL"
        if (report.notes.isDefined && report.notes.get != "")
          notesForQuery = "'" + DbUtil.safetize(report.notes.get) + "'"

        val query = """
          update report set
          author_name = '""" + DbUtil.safetize(report.authorName) + """',
          contact = '""" + DbUtil.safetize(report.contact) + """',
          is_money_in_politics_a_problem = """ + report.isMoneyInPoliticsAProblem.getOrElse("NULL") + """,
          is_supporting_amendment_to_fix_it = """ + report.isSupportingAmendmentToFixIt.getOrElse("NULL") + """,
          is_opposing_citizens_united = """ + report.isOpposingCitizensUnited.getOrElse("NULL") + """,
          has_previously_voted_for_convention = """ + report.hasPreviouslyVotedForConvention.getOrElse("NULL") + """,
          support_level = """ + supportLevelForQuery + """,
          notes = """ + notesForQuery + """
          where id = """ + report.id.get + """;"""

        Logger.info("ReportDto.update():" + query)

        SQL(query).executeUpdate()
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
