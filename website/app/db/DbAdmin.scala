package db

import play.api.db.DB
import anorm._
import play.api.Play.current
import play.api.Logger

object DbAdmin {
  def createTables() {
    createTableUsState()
    createTableTitleOfAnOfficial()
    createTablePoliticalParty()
    createTableAccount()
    createTableOfficial()
  }

  def dropTables() {
    dropTableOfficial()
    dropTableAccount()
    dropTablePoliticalParty()
    dropTableTitleOfAnOfficial()
    dropTableUsState()
  }

  def initData() {
    initDataUsStates()
    initDataTitleOfAnOfficial()
    initDataPoliticalParty()
  }

  private def createTableUsState() {
    DB.withConnection {
      implicit c =>

        val query = """
          CREATE TABLE us_state (
            id bigserial primary key,
            name VARCHAR(64) NOT NULL,
            abbr CHAR(2) NOT NULL,
            UNIQUE (name),
            UNIQUE (abbr)
          );"""

        Logger.info("DbAdmin.createTableAccount():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableTitleOfAnOfficial() {
    DB.withConnection {
      implicit c =>

        val query = """
          CREATE TABLE title_of_an_official (
            id bigserial primary key,
            name VARCHAR(64) NOT NULL,
            UNIQUE (name)
          );"""

        Logger.info("DbAdmin.createTableTitleOfAnOfficial():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTablePoliticalParty() {
    DB.withConnection {
      implicit c =>

        val query = """
          CREATE TABLE political_party (
            id bigserial primary key,
            name VARCHAR(64) NOT NULL,
            UNIQUE (name)
          );"""

        Logger.info("DbAdmin.createTablePoliticalParty():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableAccount() {
    DB.withConnection {
      implicit c =>

        val query = """
          CREATE TABLE account (
            id bigserial primary key,
            first_name VARCHAR(64) NOT NULL,
            last_name VARCHAR(64) NOT NULL,
            email_address VARCHAR(128) NOT NULL,
            password VARCHAR(1024) NOT NULL,
            us_state_id bigserial references us_state(id) NOT NULL,
            creation_timestamp integer NOT NULL,
            UNIQUE (email_address)
          );"""

        Logger.info("DbAdmin.createTableAccount():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableOfficial() {
    DB.withConnection {
      implicit c =>

        val query = """
          CREATE TABLE official (
            id bigserial primary key,
            first_name VARCHAR(64) NOT NULL,
            last_name VARCHAR(64) NOT NULL,
            title_id bigserial references title_of_an_official(id) NOT NULL,
            political_party_id bigserial references political_party(id) NOT NULL,
            us_state_id bigserial references us_state(id) NOT NULL,
            district smallint not null,
            phone_number varchar(64),
            ideology_ranking real,
            creation_timestamp integer NOT NULL
          );"""

        Logger.info("DbAdmin.createTableOfficial():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def dropTableOfficial() {
    DB.withConnection {
      implicit c =>
        val query = "drop table if exists official;"
        Logger.info("DbAdmin.dropTableOfficial(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTableAccount() {
    DB.withConnection {
      implicit c =>
        val query = "drop table if exists account;"
        Logger.info("DbAdmin.dropTableAccount(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTablePoliticalParty() {
    DB.withConnection {
      implicit c =>
        val query = "drop table if exists political_party;"
        Logger.info("DbAdmin.dropTablePoliticalParty(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTableTitleOfAnOfficial() {
    DB.withConnection {
      implicit c =>
        val query = "drop table if exists title_of_an_official;"
        Logger.info("DbAdmin.dropTableTitleOfAnOfficial(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTableUsState() {
    DB.withConnection {
      implicit c =>
        val query = "drop table if exists us_state;"
        Logger.info("DbAdmin.dropTableUsState(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def initDataUsStates() {
    DB.withConnection {
      implicit c =>
        SQL("insert into us_state(name, abbr) values('Alabama', 'AL');").execute()
        SQL("insert into us_state(name, abbr) values('Alaska', 'AK');").execute()
        SQL("insert into us_state(name, abbr) values('Arizona', 'AZ');").execute()
        SQL("insert into us_state(name, abbr) values('Arkansas', 'AR');").execute()
        SQL("insert into us_state(name, abbr) values('California', 'CA');").execute()
        SQL("insert into us_state(name, abbr) values('Colorado', 'CO');").execute()
        SQL("insert into us_state(name, abbr) values('Connecticut', 'CT');").execute()
        SQL("insert into us_state(name, abbr) values('Delaware', 'DE');").execute()
        SQL("insert into us_state(name, abbr) values('District of Columbia', 'DC');").execute()
        SQL("insert into us_state(name, abbr) values('Florida', 'FL');").execute()
        SQL("insert into us_state(name, abbr) values('Georgia', 'GA');").execute()
        SQL("insert into us_state(name, abbr) values('Hawaii', 'HI');").execute()
        SQL("insert into us_state(name, abbr) values('Idaho', 'ID');").execute()
        SQL("insert into us_state(name, abbr) values('Illinois', 'IL');").execute()
        SQL("insert into us_state(name, abbr) values('Indiana', 'IN');").execute()
        SQL("insert into us_state(name, abbr) values('Iowa', 'IA');").execute()
        SQL("insert into us_state(name, abbr) values('Kansas', 'KS');").execute()
        SQL("insert into us_state(name, abbr) values('Kentucky', 'KY');").execute()
        SQL("insert into us_state(name, abbr) values('Louisiana', 'LA');").execute()
        SQL("insert into us_state(name, abbr) values('Maine', 'ME');").execute()
        SQL("insert into us_state(name, abbr) values('Maryland', 'MD');").execute()
        SQL("insert into us_state(name, abbr) values('Massachusetts', 'MA');").execute()
        SQL("insert into us_state(name, abbr) values('Michigan', 'MI');").execute()
        SQL("insert into us_state(name, abbr) values('Minnesota', 'MN');").execute()
        SQL("insert into us_state(name, abbr) values('Mississippi', 'MS');").execute()
        SQL("insert into us_state(name, abbr) values('Missouri', 'MO');").execute()
        SQL("insert into us_state(name, abbr) values('Montana', 'MT');").execute()
        SQL("insert into us_state(name, abbr) values('Nebraska', 'NE');").execute()
        SQL("insert into us_state(name, abbr) values('Nevada', 'NV');").execute()
        SQL("insert into us_state(name, abbr) values('New Hampshire', 'NH');").execute()
        SQL("insert into us_state(name, abbr) values('New Jersey', 'NJ');").execute()
        SQL("insert into us_state(name, abbr) values('New Mexico', 'NM');").execute()
        SQL("insert into us_state(name, abbr) values('New York', 'NY');").execute()
        SQL("insert into us_state(name, abbr) values('North Carolina', 'NC');").execute()
        SQL("insert into us_state(name, abbr) values('North Dakota', 'ND');").execute()
        SQL("insert into us_state(name, abbr) values('Ohio', 'OH');").execute()
        SQL("insert into us_state(name, abbr) values('Oklahoma', 'OK');").execute()
        SQL("insert into us_state(name, abbr) values('Oregon', 'OR');").execute()
        SQL("insert into us_state(name, abbr) values('Pennsylvania', 'PA');").execute()
        SQL("insert into us_state(name, abbr) values('Rhode Island', 'RI');").execute()
        SQL("insert into us_state(name, abbr) values('South Carolina', 'SC');").execute()
        SQL("insert into us_state(name, abbr) values('South Dakota', 'SD');").execute()
        SQL("insert into us_state(name, abbr) values('Tennessee', 'TN');").execute()
        SQL("insert into us_state(name, abbr) values('Texas', 'TX');").execute()
        SQL("insert into us_state(name, abbr) values('Utah', 'UT');").execute()
        SQL("insert into us_state(name, abbr) values('Vermont', 'VT');").execute()
        SQL("insert into us_state(name, abbr) values('Virginia', 'VA');").execute()
        SQL("insert into us_state(name, abbr) values('Washington', 'WA');").execute()
        SQL("insert into us_state(name, abbr) values('West Virginia', 'WV');").execute()
        SQL("insert into us_state(name, abbr) values('Wisconsin', 'WI');").execute()
        SQL("insert into us_state(name, abbr) values('Wyoming', 'WY');").execute()
    }
  }

  private def initDataTitleOfAnOfficial() {
    DB.withConnection {
      implicit c =>
        SQL("insert into title_of_an_official(name) values('Representative');").execute()
        SQL("insert into title_of_an_official(name) values('Senator');").execute()
    }
  }

  private def initDataPoliticalParty() {
    DB.withConnection {
      implicit c =>
        SQL("insert into political_party(name) values('Republican');").execute()
        SQL("insert into political_party(name) values('Democrat');").execute()
    }
  }
}
