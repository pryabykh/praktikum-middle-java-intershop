--liquibase formatted sql

--changeset intershop:1
create table intershop.images
(
    id    bigint primary key generated always as identity,
    name  text  not null,
    bytes bytea not null
);

create table intershop.items
(
    id          bigint primary key generated always as identity,
    title       text   not null,
    price       bigint not null,
    description text   not null,
    image_id    bigint references intershop.images (id)
);