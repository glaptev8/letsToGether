create table if not exists event_review (
    id       bigserial
        constraint event_review_id_pk
            primary key,
    event_id bigint not null
        constraint event_review_event_id_fk
            references event,
    user_id  bigserial,
    review   varchar(256),
    grade    real default 0,
    constraint event_review_event_id_user_id_pk
        unique (event_id, user_id)
);