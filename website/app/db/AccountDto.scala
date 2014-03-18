package db

import anorm._
import models.Account
import play.api.db.DB
import play.api.Play.current
import play.api.Logger


object AccountDto {
  def create(account: Account): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                       insert into account(username, password)
        values('""" + DbUtil.safetize(account.username) + """',
          crypt('""" + DbUtil.safetize(account.password.get) + """', gen_salt('md5')));"""

        Logger.info("AccountDto.create():" + query)

        SQL(query).executeInsert()
    }
  }

  def delete(account: Account) {
    DB.withConnection {
      implicit c =>

        val query = """
                      delete from account
          where id = """ + account.id.get + """;"""

        Logger.info("AccountDto.delete():" + query)

        SQL(query).execute()
    }
  }

  def getOfId(id: Long): Option[Account] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select username
          from account
          where id = """ + id + """;"""

        Logger.info("AccountDto.getOfId():" + query)

        SQL(query).apply().headOption match {
          case Some(row) =>
            Some(
              Account(
                Some(id),
                row[String]("username")
              )
            )
          case None => None
        }
    }
  }

  def getOfUsername(username: String): Option[Account] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select id
          from account
          where username = '""" + DbUtil.safetize(username) + """';"""

        Logger.info("AccountDto.getOfUsername():" + query)

        SQL(query).apply().headOption match {
          case Some(row) =>
            Some(
              Account(
                row[Option[Long]]("id"),
                username
              )
            )
          case None => None
        }
    }
  }

  def getOfSignInInfo(username: String, password: String): Option[Account] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select id
          from account
          where username = '""" + DbUtil.safetize(username) + """'
          and password = crypt('""" + DbUtil.safetize(password) + """', password);"""

        /* Not logged to avoid logging clear password
        Logger.info("AccountDto.getOfSignInInfo():" + query) */

        SQL(query).apply().headOption match {
          case Some(row) =>
            Some(
              Account(
                Some(row[Long]("id")),
                username,
                Some(password)
              )
            )
          case None => None
        }
    }
  }
}
