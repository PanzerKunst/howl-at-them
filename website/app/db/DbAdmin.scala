package db

import play.api.db.DB
import anorm._
import play.api.Play.current
import play.api.Logger

object DbAdmin {
  def reCreateNonVoteSmartTables() {
    dropTable("state_legislator_extra")
    dropTable("account")
    dropTable("report")
    dropTable("us_state")

    createTableUsState()
    createTableReport()
    createTableAccount()
    createTableStateLegislatorExtra()
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
    renameTable("temp_vote_smart_candidate_office", "vote_smart_candidate_office")
    renameTable("temp_vote_smart_leadership_position", "vote_smart_leadership_position")
    renameTable("temp_vote_smart_leading_official", "vote_smart_leading_official")
    renameTable("temp_vote_smart_committee", "vote_smart_committee")
    renameTable("temp_vote_smart_committee_membership", "vote_smart_committee_membership")

    createViewStateLegislator()
  }

  private def createTempVoteSmartTables() {
    createTableVoteSmartCandidate()
    createTableVoteSmartCandidateOffice()
    createTableVoteSmartLeadershipPosition()
    createTableVoteSmartLeadingOfficial()
    createTableVoteSmartCommittee()
    createTableVoteSmartCommitteeMembership()
  }

  private def dropVoteSmartTables() {
    dropViewStateLegislator()
    dropTableVoteSmartCommitteeMembership()
    dropTableVoteSmartCommittee()
    dropTableVoteSmartLeadingOfficial()
    dropTableVoteSmartLeadershipPosition()
    dropTableVoteSmartCandidateOffice()
    dropTableVoteSmartCandidate()
  }

  private def dropTempVoteSmartTables() {
    val isTemp = true

    dropTableVoteSmartCommitteeMembership(isTemp)
    dropTableVoteSmartCommittee(isTemp)
    dropTableVoteSmartLeadingOfficial(isTemp)
    dropTableVoteSmartLeadershipPosition(isTemp)
    dropTableVoteSmartCandidateOffice(isTemp)
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

  private def createTableReport() {
    DB.withConnection {
      implicit c =>

        val query = """
          create table report (
            id bigserial primary key,
            candidate_id integer not null/* Can't have that reference because we want to be able to drop the table - references vote_smart_candidate(candidate_id) */,
            author_name varchar(64) not null,
            contact varchar(32) not null,
            is_money_in_politics_a_problem boolean,
            is_supporting_amendment_to_fix_it boolean,
            is_opposing_citizens_united boolean,
            has_previously_voted_for_convention boolean,
            support_level varchar(32),
            notes varchar(512),
            creation_timestamp bigint not null,
            is_deleted boolean not null default false
          );"""

        Logger.info("DbAdmin.createTableReport():" + query)

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
            password VARCHAR(128) NOT NULL,
            UNIQUE (username)
          );"""

        Logger.info("DbAdmin.createTableAccount():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableStateLegislatorExtra() {
    DB.withConnection {
      implicit c =>

        val query = """
          create table state_legislator_extra (
            candidate_id integer not null primary key/* Can't have that reference because we want to be able to drop the table - references vote_smart_candidate(candidate_id) */,
            other_phone_number varchar(16),
            is_a_priority_target boolean not null default false
            );"""

        Logger.info("DbAdmin.createTableStateLegislatorExtra():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableVoteSmartCandidate() {
    DB.withConnection {
      implicit c =>

        val query = """
          create table temp_vote_smart_candidate (
            candidate_id integer primary key,
            first_name varchar(32) not null,
            nick_name varchar(32),
            middle_name varchar(32),
            preferred_name varchar(32),
            last_name varchar(32) not null,
            suffix varchar(32),
            title varchar(128) not null,
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
            office_name varchar(128),
            office_type_id char(1)
          );"""

        Logger.info("DbAdmin.createTableVoteSmartCandidate():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableVoteSmartCandidateOffice() {
    DB.withConnection {
      implicit c =>

        val query = """
          create table temp_vote_smart_candidate_office (
            id bigserial primary key,
            candidate_id integer not null references temp_vote_smart_candidate(candidate_id),
            office_type varchar(32) not null,
            office_type_id integer not null,
            street varchar(256),
            city varchar(128),
            state char(2) references us_state(id),
            zip varchar(64),
            phone1 varchar(64)
            );"""

        Logger.info("DbAdmin.createTableVoteSmartCandidateOffice():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableVoteSmartLeadershipPosition() {
    DB.withConnection {
      implicit c =>

        val query = """
          create table temp_vote_smart_leadership_position (
            id bigserial primary key,
            leadership_id integer not null,
            position_name varchar(128) not null,
            office_id integer not null,
            office_name varchar(64) not null
          );"""

        Logger.info("DbAdmin.createTableVoteSmartLeadershipPosition():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableVoteSmartLeadingOfficial() {
    DB.withConnection {
      implicit c =>

        val query = """
          create table temp_vote_smart_leading_official (
            id bigserial primary key,
            leadership_id integer not null /* Can't have a FK because vote_smart_leadership_position.leadership_id is not a PK */,
            candidate_id integer not null references temp_vote_smart_candidate(candidate_id),
            position_name varchar(128) not null
          );"""

        Logger.info("DbAdmin.createTableVoteSmartLeadingOfficial():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableVoteSmartCommittee() {
    DB.withConnection {
      implicit c =>

        val query = """
          create table temp_vote_smart_committee (
            committee_id integer primary key,
            us_state_id char(2) references us_state(id),
            committee_name varchar(128) not null
            );"""

        Logger.info("DbAdmin.createTableVoteSmartCommittee():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def createTableVoteSmartCommitteeMembership() {
    DB.withConnection {
      implicit c =>

        val query = """
          create table temp_vote_smart_committee_membership (
            id bigserial primary key,
            committee_id integer not null references temp_vote_smart_committee(committee_id),
            candidate_id integer not null references temp_vote_smart_candidate(candidate_id),
            position varchar(64) not null
            );"""

        Logger.info("DbAdmin.createTableVoteSmartCommitteeMembership():" + query)

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
            lo.leadership_id as leadership_position_id,
            lo.position_name as leadership_position_name,
            o.office_type,
            o.street as office_street,
            o.city as office_city,
            o.state as office_us_state_id,
            o.zip as office_zip,
            o.phone1 as office_phone_number,
            cm.id as committee_membership_id,
            cm.position as committee_position,
            com.committee_id,
            com.us_state_id as committee_us_state_id,
            com.committee_name,
            sle.other_phone_number,
            sle.is_a_priority_target
            from vote_smart_candidate c
            left join vote_smart_leading_official lo on lo.candidate_id = c.candidate_id
            left join vote_smart_candidate_office o on o.candidate_id = c.candidate_id
            left join vote_smart_committee_membership cm on cm.candidate_id = c.candidate_id
            left join vote_smart_committee com on com.committee_id = cm.committee_id
            left join state_legislator_extra sle on sle.candidate_id = c.candidate_id
            where c.office_type_id = 'L';"""

        Logger.info("DbAdmin.createViewStateLegislator():" + query)

        SQL(query).executeUpdate()
    }
  }

  private def dropTable(tableName: String) {
    DB.withConnection {
      implicit c =>

        val query = "drop table if exists " + tableName + ";"
        Logger.info("DbAdmin.dropTable(): " + query)
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

  private def dropTableVoteSmartCandidateOffice(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = "drop table if exists " + tableNamePrefix + "vote_smart_candidate_office;"
        Logger.info("DbAdmin.dropTableVoteSmartCandidateOffice(): " + query)
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

  private def dropTableVoteSmartCommittee(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = "drop table if exists " + tableNamePrefix + "vote_smart_committee;"
        Logger.info("DbAdmin.dropTableVoteSmartCommittee(): " + query)
        SQL(query).executeUpdate()
    }
  }

  private def dropTableVoteSmartCommitteeMembership(isTemp: Boolean = false) {
    DB.withConnection {
      implicit c =>

        val tableNamePrefix = if (isTemp) "temp_" else ""

        val query = "drop table if exists " + tableNamePrefix + "vote_smart_committee_membership;"
        Logger.info("DbAdmin.dropTableVoteSmartCommitteeMembership(): " + query)
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
