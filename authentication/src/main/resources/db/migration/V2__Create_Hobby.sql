create table if not exists hobby (
    id      bigserial
        constraint hobby_id_pk
        primary key,
    user_id bigint      not null
        constraint hobby_users_id_fk
        references users,
    hobby   varchar(32) not null
);