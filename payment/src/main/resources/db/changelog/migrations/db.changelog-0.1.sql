--liquibase formatted sql

--changeset payment:1
create table payment.accounts
(
    id    bigint primary key generated always as identity,
    user_id  bigint  not null,
    balance bigint not null
);

insert into payment.accounts (user_id, balance)
values (1, 999999999);
