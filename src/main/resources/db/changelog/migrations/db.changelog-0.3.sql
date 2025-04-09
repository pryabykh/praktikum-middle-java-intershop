--liquibase formatted sql

--changeset intershop:1
create table intershop.orders
(
    id        bigint primary key generated always as identity,
    total_sum bigint                                 not null,
    user_id   bigint references intershop.users (id) not null
);

create table intershop.order_items
(
    id          bigint primary key generated always as identity,
    title       text                                    not null,
    price       bigint                                  not null,
    description text                                    not null,
    image_id    bigint references intershop.images (id) not null,
    order_id    bigint references intershop.orders (id) not null,
    count       int                                     not null
);
