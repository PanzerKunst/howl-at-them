package db

import anorm._
import models.Account
import play.api.db.DB
import play.api.Play.current
import play.api.Logger
import java.util.Date


object AccountDto {
  def create(account: Account): Option[Long] = {
    DB.withConnection {
      implicit c =>

        val query = """
                       insert into account(first_name, last_name, email_address, password, us_state_id, creation_timestamp)
        values('""" + DbUtil.backslashQuotes(account.firstName) + """', '""" +
          DbUtil.backslashQuotes(account.lastName) + """', '""" +
          DbUtil.backslashQuotes(account.emailAddress) + """',
          crypt('""" + DbUtil.backslashQuotes(account.password.get) + """', gen_salt('md5')), """ +
          account.usStateId + """, """ +
          (new Date().getTime / 1000) + """);"""

        Logger.info("AccountDto.create():" + query)

        SQL(query).executeInsert()
    }
  }

  def update(account: Account) {
    DB.withConnection {
      implicit c =>

        var query = """
                       update account set
        first_name = '""" + DbUtil.backslashQuotes(account.firstName) + """',
        last_name = '""" + DbUtil.backslashQuotes(account.lastName) + """',
        """

        if (account.password.isDefined)
          query = query + """password = crypt('""" + DbUtil.backslashQuotes(account.password.get) + """', gen_salt('md5')),
            """

        query = query + """us_state_id = """ + account.usStateId + """
        where id = """ + account.id.get + """;"""

        Logger.info("AccountDto.update():" + query)

        SQL(query).executeUpdate()
    }
  }

  def getOfId(id: Long): Option[Account] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select first_name, last_name, email_address, password, us_state_id, creation_timestamp
          from account
          where id = """ + id + """;"""

        Logger.info("AccountDto.getOfId():" + query)

        SQL(query).apply().headOption match {
          case Some(sqlRow) =>
            Some(
              new Account(
                Some(id),
                sqlRow[String]("first_name"),
                sqlRow[String]("last_name"),
                sqlRow[String]("email_address"),
                sqlRow[Option[String]]("password"),
                sqlRow[Long]("us_state_id"),
                Some(sqlRow[Int]("creation_timestamp"))
              )
            )
          case None => None
        }
    }
  }

  def getOfEmail(emailAddress: String): Option[Account] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select id, first_name, last_name, password, us_state_id, creation_timestamp
          from account
          where email_address = '""" + DbUtil.backslashQuotes(emailAddress) + """';"""

        Logger.info("AccountDto.getOfEmail():" + query)

        SQL(query).apply().headOption match {
          case Some(sqlRow) =>
            Some(
              new Account(
                Some(sqlRow[Long]("id")),
                sqlRow[String]("first_name"),
                sqlRow[String]("last_name"),
                emailAddress,
                sqlRow[Option[String]]("password"),
                sqlRow[Long]("us_state_id"),
                Some(sqlRow[Int]("creation_timestamp"))
              )
            )
          case None => None
        }
    }
  }

  def getOfSignInInfo(emailAddress: String, password: String): Option[Account] = {
    DB.withConnection {
      implicit c =>

        val query = """
          select id, first_name, last_name, us_state_id, creation_timestamp
          from account
          where email_address = '""" + DbUtil.backslashQuotes(emailAddress) + """'
          and password = crypt('""" + DbUtil.backslashQuotes(password) + """', password);"""

        Logger.info("AccountDto.getOfSignInInfo():" + query)

        SQL(query).apply().headOption match {
          case Some(sqlRow) =>
            Some(
              new Account(
                Some(sqlRow[Long]("id")),
                sqlRow[String]("first_name"),
                sqlRow[String]("last_name"),
                emailAddress,
                Some(password),
                sqlRow[Long]("us_state_id"),
                Some(sqlRow[Int]("creation_timestamp"))
              )
            )
          case None => None
        }
    }
  }
}
