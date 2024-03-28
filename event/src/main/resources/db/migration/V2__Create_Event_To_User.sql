create table event_to_user (
    id         bigserial
        constraint event_to_user_id_pk
            primary key,
    event_id   bigint  not null
        constraint event_to_user_event_id_fk
            references event,
    user_id    bigint  not null,
    subscribed boolean not null,
    constraint event_to_user_user_id_event_id_pk
        unique (user_id, event_id)
);
