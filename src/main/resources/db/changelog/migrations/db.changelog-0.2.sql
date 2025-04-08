--liquibase formatted sql

--changeset intershop:1
create table intershop.users
(
    id   bigint primary key generated always as identity,
    name text not null
);
insert into intershop.users (name)
values ('ADMIN');

create table intershop.carts
(
    id      bigint primary key generated always as identity,
    user_id bigint references intershop.users (id) not null,
    item_id bigint references intershop.items (id) not null,
    count   int not null default 0
);
