alter table us_states rename to us_state;

create table vote_smart_candidate_bio (
            candidate_id integer primary key references vote_smart_candidate(candidate_id),
            photo varchar(128) not null,
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
            office_next_elect smallint /* year */,
            office_term_start date,
            office_term_end date
            );

create table vote_smart_candidate_office (
            id bigserial primary key,
            candidate_id integer not null references vote_smart_candidate(candidate_id),
            office_type varchar(32) not null,
            office_type_id integer not null,
            street varchar(256) not null,
            city varchar(128) not null,
            state char(2) not null references us_state(id),
            zip varchar(64) not null,
            phone1 varchar(32) not null
            );

create table vote_smart_candidate_committee (
            id bigserial primary key,
            candidate_id integer not null references vote_smart_candidate(candidate_id),
            committee_id integer not null,
            committee_name varchar(64) not null
            );

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
            left join vote_smart_candidate_committee cc on cc.candidate_id = c.candidate_id;

select * from state_legislators
where title = 'Assembly Member'
limit 100;
