package db

import play.api.db.DB
import anorm._
import play.api.Play.current
import play.api.Logger

object DbAdmin {
  def createTables() {
    createTableUsStates()
    createTableStateLegislatorTitles()
    createTablePoliticalParties()
    createTableAccounts()
    createTableStateLegislators()
  }

  def dropTables() {
    dropTableStateLegislators()
    dropTableAccounts()
    dropTablePoliticalParties()
    dropTableStateLegislatorTitles()
    dropTableUsStates()
  }

  def initData() {
    initDataUsStates()
    initDataStateLegislatorTitles()
    initDataPoliticalParties()
  }

  private def createTableUsStates() {
    DB.withConnection {
      implicit c =>

        val query = """
          CREATE TABLE us_states (
            id bigserial primary key,
            name VARCHAR(64) NOT NULL,
            abbr CHAR(2) NOT NULL,
            UNIQUE (name),
            UNIQUE (abbr)
          );"""

        Logger.info("DbAdmin.createTableUsStates():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableStateLegislatorTitles() {
    DB.withConnection {
      implicit c =>

        val query = """
          CREATE TABLE state_legislator_titles (
            id bigserial primary key,
            name VARCHAR(64) NOT NULL,
            UNIQUE (name)
          );"""

        Logger.info("DbAdmin.createTableStateLegislatorTitles():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTablePoliticalParties() {
    DB.withConnection {
      implicit c =>

        val query = """
          CREATE TABLE political_parties (
            id bigserial primary key,
            name VARCHAR(64) NOT NULL,
            UNIQUE (name)
          );"""

        Logger.info("DbAdmin.createTablePoliticalParties():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableAccounts() {
    DB.withConnection {
      implicit c =>

        val query = """
          CREATE TABLE accounts (
            id bigserial primary key,
            first_name VARCHAR(64) NOT NULL,
            last_name VARCHAR(64) NOT NULL,
            email_address VARCHAR(128) NOT NULL,
            password VARCHAR(1024) NOT NULL,
            us_state_id bigserial references us_states(id) NOT NULL,
            creation_timestamp integer NOT NULL,
            UNIQUE (email_address)
          );"""

        Logger.info("DbAdmin.createTableAccounts():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableStateLegislators() {
    DB.withConnection {
      implicit c =>

        val query = """
          CREATE TABLE state_legislators (
            id bigserial primary key,
            first_name VARCHAR(64) NOT NULL,
            last_name VARCHAR(64) NOT NULL,
            title_id bigserial references state_legislator_titles(id) NOT NULL,
            political_party_id bigserial references political_parties(id) NOT NULL,
            us_state_id bigserial references us_states(id) NOT NULL,
            district smallint not null,
            phone_number varchar(64),
            ideology_ranking real,
            creation_timestamp integer NOT NULL
          );"""

        Logger.info("DbAdmin.createTableStateLegislators():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def dropTableStateLegislators() {
    DB.withConnection {
      implicit c =>
        val query = "drop table if exists state_legislators;"
        Logger.info("DbAdmin.dropTableStateLegislators(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTableAccounts() {
    DB.withConnection {
      implicit c =>
        val query = "drop table if exists accounts;"
        Logger.info("DbAdmin.dropTableAccounts(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTablePoliticalParties() {
    DB.withConnection {
      implicit c =>
        val query = "drop table if exists political_parties;"
        Logger.info("DbAdmin.dropTablePoliticalParties(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTableStateLegislatorTitles() {
    DB.withConnection {
      implicit c =>
        val query = "drop table if exists state_legislator_titles;"
        Logger.info("DbAdmin.dropTableStateLegislatorTitles(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTableUsStates() {
    DB.withConnection {
      implicit c =>
        val query = "drop table if exists us_states;"
        Logger.info("DbAdmin.dropTableUsStates(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def initDataUsStates() {
    DB.withConnection {
      implicit c =>
        SQL("insert into us_states(name, abbr) values('Alabama', 'AL');").execute()
        SQL("insert into us_states(name, abbr) values('Alaska', 'AK');").execute()
        SQL("insert into us_states(name, abbr) values('Arizona', 'AZ');").execute()
        SQL("insert into us_states(name, abbr) values('Arkansas', 'AR');").execute()
        SQL("insert into us_states(name, abbr) values('California', 'CA');").execute()
        SQL("insert into us_states(name, abbr) values('Colorado', 'CO');").execute()
        SQL("insert into us_states(name, abbr) values('Connecticut', 'CT');").execute()
        SQL("insert into us_states(name, abbr) values('Delaware', 'DE');").execute()
        SQL("insert into us_states(name, abbr) values('District of Columbia', 'DC');").execute()
        SQL("insert into us_states(name, abbr) values('Florida', 'FL');").execute()
        SQL("insert into us_states(name, abbr) values('Georgia', 'GA');").execute()
        SQL("insert into us_states(name, abbr) values('Hawaii', 'HI');").execute()
        SQL("insert into us_states(name, abbr) values('Idaho', 'ID');").execute()
        SQL("insert into us_states(name, abbr) values('Illinois', 'IL');").execute()
        SQL("insert into us_states(name, abbr) values('Indiana', 'IN');").execute()
        SQL("insert into us_states(name, abbr) values('Iowa', 'IA');").execute()
        SQL("insert into us_states(name, abbr) values('Kansas', 'KS');").execute()
        SQL("insert into us_states(name, abbr) values('Kentucky', 'KY');").execute()
        SQL("insert into us_states(name, abbr) values('Louisiana', 'LA');").execute()
        SQL("insert into us_states(name, abbr) values('Maine', 'ME');").execute()
        SQL("insert into us_states(name, abbr) values('Maryland', 'MD');").execute()
        SQL("insert into us_states(name, abbr) values('Massachusetts', 'MA');").execute()
        SQL("insert into us_states(name, abbr) values('Michigan', 'MI');").execute()
        SQL("insert into us_states(name, abbr) values('Minnesota', 'MN');").execute()
        SQL("insert into us_states(name, abbr) values('Mississippi', 'MS');").execute()
        SQL("insert into us_states(name, abbr) values('Missouri', 'MO');").execute()
        SQL("insert into us_states(name, abbr) values('Montana', 'MT');").execute()
        SQL("insert into us_states(name, abbr) values('Nebraska', 'NE');").execute()
        SQL("insert into us_states(name, abbr) values('Nevada', 'NV');").execute()
        SQL("insert into us_states(name, abbr) values('New Hampshire', 'NH');").execute()
        SQL("insert into us_states(name, abbr) values('New Jersey', 'NJ');").execute()
        SQL("insert into us_states(name, abbr) values('New Mexico', 'NM');").execute()
        SQL("insert into us_states(name, abbr) values('New York', 'NY');").execute()
        SQL("insert into us_states(name, abbr) values('North Carolina', 'NC');").execute()
        SQL("insert into us_states(name, abbr) values('North Dakota', 'ND');").execute()
        SQL("insert into us_states(name, abbr) values('Ohio', 'OH');").execute()
        SQL("insert into us_states(name, abbr) values('Oklahoma', 'OK');").execute()
        SQL("insert into us_states(name, abbr) values('Oregon', 'OR');").execute()
        SQL("insert into us_states(name, abbr) values('Pennsylvania', 'PA');").execute()
        SQL("insert into us_states(name, abbr) values('Rhode Island', 'RI');").execute()
        SQL("insert into us_states(name, abbr) values('South Carolina', 'SC');").execute()
        SQL("insert into us_states(name, abbr) values('South Dakota', 'SD');").execute()
        SQL("insert into us_states(name, abbr) values('Tennessee', 'TN');").execute()
        SQL("insert into us_states(name, abbr) values('Texas', 'TX');").execute()
        SQL("insert into us_states(name, abbr) values('Utah', 'UT');").execute()
        SQL("insert into us_states(name, abbr) values('Vermont', 'VT');").execute()
        SQL("insert into us_states(name, abbr) values('Virginia', 'VA');").execute()
        SQL("insert into us_states(name, abbr) values('Washington', 'WA');").execute()
        SQL("insert into us_states(name, abbr) values('West Virginia', 'WV');").execute()
        SQL("insert into us_states(name, abbr) values('Wisconsin', 'WI');").execute()
        SQL("insert into us_states(name, abbr) values('Wyoming', 'WY');").execute()
    }
  }

  private def initDataStateLegislatorTitles() {
    DB.withConnection {
      implicit c =>
        SQL("insert into state_legislator_titles(name) values('Representative');").execute()
        SQL("insert into state_legislator_titles(name) values('Senator');").execute()
    }
  }

  private def initDataPoliticalParties() {
    DB.withConnection {
      implicit c =>
        SQL("insert into political_parties(name) values('Republican');").execute()
        SQL("insert into political_parties(name) values('Democrat');").execute()
    }
  }
}
