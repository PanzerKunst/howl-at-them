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

  def getOfId(id: Long): Option[Account] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select username, password
          from account
          where id = """ + id + """;"""

        Logger.info("AccountDto.getOfId():" + query)

        SQL(query).apply().headOption match {
          case Some(sqlRow) =>
            Some(
              new Account(
                Some(id),
                sqlRow[String]("username"),
                sqlRow[Option[String]]("password")
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
          case Some(sqlRow) =>
            Some(
              new Account(
                Some(sqlRow[Long]("id")),
                username,
                Some(password)
              )
            )
          case None => None
        }
    }
  }
}
