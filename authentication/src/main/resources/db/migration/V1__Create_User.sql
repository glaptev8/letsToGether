create table if not exists public.users
(
    id         bigserial
        constraint users_id_pk
        primary key,
    first_name varchar(128) not null,
    last_name  varchar(128) not null,
    age        integer,
    about_me  varchar(256),
    path_to_avatar varchar(4096),
    gender     varchar(32),
    email      varchar(128) not null,
    phone      varchar(128),
    provider_id varchar(512) not null,
    created_at TIMESTAMP not null default now()
);
