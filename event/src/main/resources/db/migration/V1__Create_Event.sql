create table if not exists event (
    id              bigserial
        constraint event_id_pk
            primary key,
    name            varchar(16)   not null,
    status          varchar(32)   not null,
    start_date      timestamp     not null,
    end_date        timestamp,
    created_at      timestamp default now(),
    description     varchar(1024) not null,
    address         varchar(1024) not null,
    lng             real          not null,
    lat             real          not null,
    creator_id      bigint        not null,
    min_participant integer,
    max_participant integer,
    activity_type   varchar(254)  not null
);