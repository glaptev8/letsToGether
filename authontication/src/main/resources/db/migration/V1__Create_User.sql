create table if not exists public.users
(
    id         bigserial
        constraint users_id_pk
        primary key,
    first_name varchar(128) not null,
    last_name  varchar(128) not null,
    password varchar(2048) not null,
    age        integer      not null,
    gender     varchar(32),
    email      varchar(128),
    phone      varchar(128) not null,
    created_at TIMESTAMP not null default now()
);
