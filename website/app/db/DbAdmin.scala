package db

import play.api.db.DB
import anorm._
import play.api.Play.current
import play.api.Logger

object DbAdmin {
  def reCreateNonVoteSmartTables() {
    dropTableAccount()
    dropTableUsState()

    createTableUsState()
    createTableAccount()
  }

  def initData() {
    initDataUsState()
  }

  def reCreateTempVoteSmartTables() {
    dropTempVoteSmartTables()
    createTempVoteSmartTables()
  }

  def replaceVoteSmartTables() {
    dropVoteSmartTables()

    renameTable("temp_vote_smart_candidate", "vote_smart_candidate")
    renameTable("temp_vote_smart_candidate_bio", "vote_smart_candidate_bio")
    renameTable("temp_vote_smart_candidate_office", "vote_smart_candidate_office")
    renameTable("temp_vote_smart_candidate_committee", "vote_smart_candidate_committee")
    renameTable("temp_vote_smart_leadership_position", "vote_smart_leadership_position")
    renameTable("temp_vote_smart_leading_official", "vote_smart_leading_official")

    createViewStateLegislator()
  }

  private def createVoteSmartTables(isTemp: Boolean = false) {
    createTableVoteSmartCandidate(isTemp)
    createTableVoteSmartCandidateBio(isTemp)
    createTableVoteSmartCandidateOffice(isTemp)
    createTableVoteSmartCandidateCommittee(isTemp)
    createTableVoteSmartLeadershipPosition(isTemp)
    createTableVoteSmartLeadingOfficial(isTemp)
  }

  private def createTempVoteSmartTables() {
    val isTemp = true
    createVoteSmartTables(isTemp)
  }

  private def dropVoteSmartTables() {
    dropViewStateLegislator()
    dropTableVoteSmartLeadingOfficial()
    dropTableVoteSmartLeadershipPosition()
    dropTableVoteSmartCandidateCommittee()
    dropTableVoteSmartCandidateOffice()
    dropTableVoteSmartCandidateBio()
    dropTableVoteSmartCandidate()
  }

  private def dropTempVoteSmartTables() {
    val isTemp = true

    dropTableVoteSmartLeadingOfficial(isTemp)
    dropTableVoteSmartLeadershipPosition(isTemp)
    dropTableVoteSmartCandidateCommittee(isTemp)
    dropTableVoteSmartCandidateOffice(isTemp)
    dropTableVoteSmartCandidateBio(isTemp)
    dropTableVoteSmartCandidate(isTemp)
  }

  private def createTableUsState() {
    DB.withConnection {
      implicit c =>

        val query = """
          CREATE TABLE us_state (
            id CHAR(2) primary key,
            name VARCHAR(64) NOT NULL,
            UNIQUE (name)
          );"""

        Logger.info("DbAdmin.createTableUsState():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableVoteSmartCandidate(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = """
          create table """ + tableNamePrefix + """vote_smart_candidate (
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
            election_state_id char(2) references us_state(id),
            election_office_type_id char(1),
            election_year smallint,
            office_parties varchar(128),
            office_district_id integer,
            office_district_name varchar(64),
            office_state_id char(2) references us_state(id),
            office_id smallint,
            office_name varchar(32),
            office_type_id char(1)
          );"""

        Logger.info("DbAdmin.createTableVoteSmartCandidate():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableVoteSmartCandidateBio(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = """
          create table """ + tableNamePrefix + """vote_smart_candidate_bio (
            candidate_id integer primary key references """ + tableNamePrefix + """vote_smart_candidate(candidate_id)/* Next columns are not used,
            photo varchar(128),
            birth_date date not null,
            birth_place varchar(128) not null,
            gender varchar(8) not null,
            home_city varchar(128),
            home_state char(2),
            religion varchar(32),
            special_msg varchar(32),
            office_name varchar(32) not null,
            office_type varchar(32) not null,
            office_status varchar(16) not null,
            office_first_elect date,
            office_last_elect date,
            office_next_elect smallint *//* year *//*,
            office_term_start date,
            office_term_end date*/
            );"""

        Logger.info("DbAdmin.createTableVoteSmartCandidateBio():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableVoteSmartCandidateOffice(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = """
          create table """ + tableNamePrefix + """vote_smart_candidate_office (
            id bigserial primary key,
            candidate_id integer not null references """ + tableNamePrefix + """vote_smart_candidate(candidate_id),
            office_type varchar(32) not null,
            office_type_id integer not null,
            street varchar(256),
            city varchar(128),
            state char(2) references us_state(id),
            zip varchar(64),
            phone1 varchar(32)
            );"""

        Logger.info("DbAdmin.createTableVoteSmartCandidateOffice():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableVoteSmartCandidateCommittee(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = """
          create table """ + tableNamePrefix + """vote_smart_candidate_committee (
            id bigserial primary key,
            candidate_id integer not null references """ + tableNamePrefix + """vote_smart_candidate(candidate_id),
            committee_id integer not null,
            committee_name varchar(128) not null
            );"""

        Logger.info("DbAdmin.createTableVoteSmartCandidateCommittee():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableVoteSmartLeadershipPosition(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = """
          create table """ + tableNamePrefix + """vote_smart_leadership_position (
            id bigserial primary key,
            leadership_id integer not null,
            position_name varchar(128) not null,
            office_id integer not null,
            office_name varchar(32) not null
          );"""

        Logger.info("DbAdmin.createTableVoteSmartLeadershipPosition():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableVoteSmartLeadingOfficial(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = """
          create table """ + tableNamePrefix + """vote_smart_leading_official (
            id bigserial primary key,
            leadership_id integer not null /* Can't have a FK because lp.leadership_id is not a PK references vote_smart_leadership_positions(leadership_id)*/,
            state_id char(2) not null references us_state(id),
            candidate_id integer not null references """ + tableNamePrefix + """vote_smart_candidate(candidate_id),
            first_name varchar(32),
            middle_name varchar(32),
            last_name varchar(32),
            suffix varchar(32),
            position_name varchar(128) not null,
            office_id integer not null,
            candidate_title varchar(32) not null
          );"""

        Logger.info("DbAdmin.createTableVoteSmartLeadingOfficial():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableAccount() {
    DB.withConnection {
      implicit c =>

        val query = """
          CREATE TABLE account (
            id bigserial primary key,
            username VARCHAR(64) NOT NULL,
            password VARCHAR(1024) NOT NULL,
            UNIQUE (username)
          );"""

        Logger.info("DbAdmin.createTableAccount():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createViewStateLegislator() {
    DB.withConnection {
      implicit c =>

        val query = """
          create view state_legislator as
            select c.candidate_id as id,
            c.first_name,
            c.last_name,
            c.title,
            c.office_parties as political_parties,
            c.office_state_id as us_state_id,
            c.office_district_name as district,
            o.office_type,
            o.street,
            o.city,
            o.zip,
            o.phone1 as phone_number,
            cb.photo as photo_url,
            cc.committee_id,
            cc.committee_name
            from vote_smart_candidate c
            left join vote_smart_candidate_office o on o.candidate_id = c.candidate_id
            left join vote_smart_candidate_bio cb on cb.candidate_id = c.candidate_id
            left join vote_smart_candidate_committee cc on cc.candidate_id = c.candidate_id;"""

        Logger.info("DbAdmin.createViewStateLegislator():" + query)

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

  private def dropTableVoteSmartCandidate(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""
        
        val query = "drop table if exists " + tableNamePrefix + "vote_smart_candidate;"
        Logger.info("DbAdmin.dropTableVoteSmartCandidate(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTableVoteSmartCandidateBio(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = "drop table if exists " + tableNamePrefix + "vote_smart_candidate_bio;"
        Logger.info("DbAdmin.dropTableVoteSmartCandidateBio(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTableVoteSmartCandidateOffice(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = "drop table if exists " + tableNamePrefix + "vote_smart_candidate_office;"
        Logger.info("DbAdmin.dropTableVoteSmartCandidateOffice(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTableVoteSmartCandidateCommittee(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = "drop table if exists " + tableNamePrefix + "vote_smart_candidate_committee;"
        Logger.info("DbAdmin.dropTableVoteSmartCandidateCommittee(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTableVoteSmartLeadershipPosition(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = "drop table if exists " + tableNamePrefix + "vote_smart_leadership_position;"
        Logger.info("DbAdmin.dropTableVoteSmartLeadershipPosition(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTableVoteSmartLeadingOfficial(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = "drop table if exists " + tableNamePrefix + "vote_smart_leading_official;"
        Logger.info("DbAdmin.dropTableVoteSmartLeadingOfficial(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropViewStateLegislator() {
    DB.withConnection {
      implicit c =>

        val query = "drop view if exists state_legislator;"
        Logger.info("DbAdmin.dropViewStateLegislator(): " + query)
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

  private def initDataUsState() {
    DB.withConnection {
      implicit c =>

        SQL("insert into us_state(id, name) values('AL', 'Alabama');").execute()
        SQL("insert into us_state(id, name) values('AK', 'Alaska');").execute()
        SQL("insert into us_state(id, name) values('AZ', 'Arizona');").execute()
        SQL("insert into us_state(id, name) values('AR', 'Arkansas');").execute()
        SQL("insert into us_state(id, name) values('CA', 'California');").execute()
        SQL("insert into us_state(id, name) values('CO', 'Colorado');").execute()
        SQL("insert into us_state(id, name) values('CT', 'Connecticut');").execute()
        SQL("insert into us_state(id, name) values('DE', 'Delaware');").execute()
        SQL("insert into us_state(id, name) values('DC', 'District of Columbia');").execute()
        SQL("insert into us_state(id, name) values('FL', 'Florida');").execute()
        SQL("insert into us_state(id, name) values('GA', 'Georgia');").execute()
        SQL("insert into us_state(id, name) values('HI', 'Hawaii');").execute()
        SQL("insert into us_state(id, name) values('ID', 'Idaho');").execute()
        SQL("insert into us_state(id, name) values('IL', 'Illinois');").execute()
        SQL("insert into us_state(id, name) values('IN', 'Indiana');").execute()
        SQL("insert into us_state(id, name) values('IA', 'Iowa');").execute()
        SQL("insert into us_state(id, name) values('KS', 'Kansas');").execute()
        SQL("insert into us_state(id, name) values('KY', 'Kentucky');").execute()
        SQL("insert into us_state(id, name) values('LA', 'Louisiana');").execute()
        SQL("insert into us_state(id, name) values('ME', 'Maine');").execute()
        SQL("insert into us_state(id, name) values('MD', 'Maryland');").execute()
        SQL("insert into us_state(id, name) values('MA', 'Massachusetts');").execute()
        SQL("insert into us_state(id, name) values('MI', 'Michigan');").execute()
        SQL("insert into us_state(id, name) values('MN', 'Minnesota');").execute()
        SQL("insert into us_state(id, name) values('MS', 'Mississippi');").execute()
        SQL("insert into us_state(id, name) values('MO', 'Missouri');").execute()
        SQL("insert into us_state(id, name) values('MT', 'Montana');").execute()
        SQL("insert into us_state(id, name) values('NE', 'Nebraska');").execute()
        SQL("insert into us_state(id, name) values('NV', 'Nevada');").execute()
        SQL("insert into us_state(id, name) values('NH', 'New Hampshire');").execute()
        SQL("insert into us_state(id, name) values('NJ', 'New Jersey');").execute()
        SQL("insert into us_state(id, name) values('NM', 'New Mexico');").execute()
        SQL("insert into us_state(id, name) values('NY', 'New York');").execute()
        SQL("insert into us_state(id, name) values('NC', 'North Carolina');").execute()
        SQL("insert into us_state(id, name) values('ND', 'North Dakota');").execute()
        SQL("insert into us_state(id, name) values('OH', 'Ohio');").execute()
        SQL("insert into us_state(id, name) values('OK', 'Oklahoma');").execute()
        SQL("insert into us_state(id, name) values('OR', 'Oregon');").execute()
        SQL("insert into us_state(id, name) values('PA', 'Pennsylvania');").execute()
        SQL("insert into us_state(id, name) values('RI', 'Rhode Island');").execute()
        SQL("insert into us_state(id, name) values('SC', 'South Carolina');").execute()
        SQL("insert into us_state(id, name) values('SD', 'South Dakota');").execute()
        SQL("insert into us_state(id, name) values('TN', 'Tennessee');").execute()
        SQL("insert into us_state(id, name) values('TX', 'Texas');").execute()
        SQL("insert into us_state(id, name) values('UT', 'Utah');").execute()
        SQL("insert into us_state(id, name) values('VT', 'Vermont');").execute()
        SQL("insert into us_state(id, name) values('VA', 'Virginia');").execute()
        SQL("insert into us_state(id, name) values('WA', 'Washington');").execute()
        SQL("insert into us_state(id, name) values('WV', 'West Virginia');").execute()
        SQL("insert into us_state(id, name) values('WI', 'Wisconsin');").execute()
        SQL("insert into us_state(id, name) values('WY', 'Wyoming');").execute()
    }
  }

  private def renameTable(from: String, to: String) {
    DB.withConnection {
      implicit c =>

        val query = "alter table " + from + " rename to " + to + ";"
        Logger.info("DbAdmin.renameTable(): " + query)
        SQL(query).executeUpdate()
    }
  }
}
