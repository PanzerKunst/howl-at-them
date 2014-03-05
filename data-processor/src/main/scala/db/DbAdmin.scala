package db

import anorm._
import scalikejdbc.ConnectionPool
import com.typesafe.scalalogging.slf4j.Logging

object DbAdmin extends Logging {
  def createTables() {
    createTableUsStates()
    createTableVoteSmartCandidates()
  }

  def dropTables() {
    dropTableVoteSmartCandidates()
    dropTableUsStates()
  }

  def initData() {
    initDataUsStates()
  }

  private def createTableUsStates() {
    implicit val c = ConnectionPool.borrow()

    val query = """
          CREATE TABLE us_states (
            id CHAR(2) primary key,
            name VARCHAR(64) NOT NULL,
            UNIQUE (name)
          );"""

    logger.info("DbAdmin.createTableUsStates():" + query)

    try {
      SQL(query).executeUpdate()
    }
    finally {
      c.close()
    }
  }

  private def createTableVoteSmartCandidates() {
    implicit val c = ConnectionPool.borrow()

    val query = """
          create table vote_smart_candidates (
            candidate_id integer primary key,
            first_name varchar(32) not null,
            nick_name varchar(32),
            middle_name varchar(32),
            preferred_name varchar(32),
            last_name varchar(32) not null,
            suffix varchar(32),
            title varchar(64) not null,
            ballot_name varchar(64),
            election_parties varchar(32),
            election_district_id integer,
            election_district_name varchar(64),
            election_office varchar(64),
            election_office_id integer,
            election_state_id char(2) references us_states(id),
            election_office_type_id char(1),
            election_year smallint,
            office_parties varchar(128),
            office_district_id integer,
            office_district_name varchar(64),
            office_state_id char(2) references us_states(id),
            office_id smallint,
            office_name varchar(32),
            office_type_id char(1)
          );"""

    logger.info("DbAdmin.createTableVoteSmartCandidates():" + query)

    try {
      SQL(query).executeUpdate()
    }
    finally {
      c.close()
    }
  }

  private def dropTableVoteSmartCandidates() {
    implicit val c = ConnectionPool.borrow()

    val query = "drop table if exists vote_smart_candidates;"

    logger.info("DbAdmin.dropTableVoteSmartCandidates(): " + query)

    try {
      SQL(query).executeUpdate()
    }
    finally {
      c.close()
    }
  }

  private def dropTableUsStates() {
    implicit val c = ConnectionPool.borrow()

    val query = "drop table if exists us_states;"

    logger.info("DbAdmin.dropTableUsStates(): " + query)

    try {
      SQL(query).executeUpdate()
    }
    finally {
      c.close()
    }
  }

  private def initDataUsStates() {
    implicit val c = ConnectionPool.borrow()

    try {
      SQL("insert into us_states(id, name) values('AL', 'Alabama');").execute()
      SQL("insert into us_states(id, name) values('AK', 'Alaska');").execute()
      SQL("insert into us_states(id, name) values('AZ', 'Arizona');").execute()
      SQL("insert into us_states(id, name) values('AR', 'Arkansas');").execute()
      SQL("insert into us_states(id, name) values('CA', 'California');").execute()
      SQL("insert into us_states(id, name) values('CO', 'Colorado');").execute()
      SQL("insert into us_states(id, name) values('CT', 'Connecticut');").execute()
      SQL("insert into us_states(id, name) values('DE', 'Delaware');").execute()
      SQL("insert into us_states(id, name) values('DC', 'District of Columbia');").execute()
      SQL("insert into us_states(id, name) values('FL', 'Florida');").execute()
      SQL("insert into us_states(id, name) values('GA', 'Georgia');").execute()
      SQL("insert into us_states(id, name) values('HI', 'Hawaii');").execute()
      SQL("insert into us_states(id, name) values('ID', 'Idaho');").execute()
      SQL("insert into us_states(id, name) values('IL', 'Illinois');").execute()
      SQL("insert into us_states(id, name) values('IN', 'Indiana');").execute()
      SQL("insert into us_states(id, name) values('IA', 'Iowa');").execute()
      SQL("insert into us_states(id, name) values('KS', 'Kansas');").execute()
      SQL("insert into us_states(id, name) values('KY', 'Kentucky');").execute()
      SQL("insert into us_states(id, name) values('LA', 'Louisiana');").execute()
      SQL("insert into us_states(id, name) values('ME', 'Maine');").execute()
      SQL("insert into us_states(id, name) values('MD', 'Maryland');").execute()
      SQL("insert into us_states(id, name) values('MA', 'Massachusetts');").execute()
      SQL("insert into us_states(id, name) values('MI', 'Michigan');").execute()
      SQL("insert into us_states(id, name) values('MN', 'Minnesota');").execute()
      SQL("insert into us_states(id, name) values('MS', 'Mississippi');").execute()
      SQL("insert into us_states(id, name) values('MO', 'Missouri');").execute()
      SQL("insert into us_states(id, name) values('MT', 'Montana');").execute()
      SQL("insert into us_states(id, name) values('NE', 'Nebraska');").execute()
      SQL("insert into us_states(id, name) values('NV', 'Nevada');").execute()
      SQL("insert into us_states(id, name) values('NH', 'New Hampshire');").execute()
      SQL("insert into us_states(id, name) values('NJ', 'New Jersey');").execute()
      SQL("insert into us_states(id, name) values('NM', 'New Mexico');").execute()
      SQL("insert into us_states(id, name) values('NY', 'New York');").execute()
      SQL("insert into us_states(id, name) values('NC', 'North Carolina');").execute()
      SQL("insert into us_states(id, name) values('ND', 'North Dakota');").execute()
      SQL("insert into us_states(id, name) values('OH', 'Ohio');").execute()
      SQL("insert into us_states(id, name) values('OK', 'Oklahoma');").execute()
      SQL("insert into us_states(id, name) values('OR', 'Oregon');").execute()
      SQL("insert into us_states(id, name) values('PA', 'Pennsylvania');").execute()
      SQL("insert into us_states(id, name) values('RI', 'Rhode Island');").execute()
      SQL("insert into us_states(id, name) values('SC', 'South Carolina');").execute()
      SQL("insert into us_states(id, name) values('SD', 'South Dakota');").execute()
      SQL("insert into us_states(id, name) values('TN', 'Tennessee');").execute()
      SQL("insert into us_states(id, name) values('TX', 'Texas');").execute()
      SQL("insert into us_states(id, name) values('UT', 'Utah');").execute()
      SQL("insert into us_states(id, name) values('VT', 'Vermont');").execute()
      SQL("insert into us_states(id, name) values('VA', 'Virginia');").execute()
      SQL("insert into us_states(id, name) values('WA', 'Washington');").execute()
      SQL("insert into us_states(id, name) values('WV', 'West Virginia');").execute()
      SQL("insert into us_states(id, name) values('WI', 'Wisconsin');").execute()
      SQL("insert into us_states(id, name) values('WY', 'Wyoming');").execute()
    }
    finally {
      c.close()
    }
  }
}
