--liquibase formatted sql

--changeset payment:1
insert into payment.accounts (user_id, balance)
values (2, 999999999);
insert into payment.accounts (user_id, balance)
values (3, 999999999);
insert into payment.accounts (user_id, balance)
values (4, 999999999);
insert into payment.accounts (user_id, balance)
values (5, 999999999);
insert into payment.accounts (user_id, balance)
values (6, 999999999);
insert into payment.accounts (user_id, balance)
values (7, 999999999);
insert into payment.accounts (user_id, balance)
values (8, 999999999);
insert into payment.accounts (user_id, balance)
values (9, 999999999);
insert into payment.accounts (user_id, balance)
values (10, 999999999);

