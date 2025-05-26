--liquibase formatted sql

--changeset intershop:1
alter table intershop.users add column password text;
alter table intershop.users add column role text;

--Пароль: test123
update intershop.users
set
password = '$2a$10$4MqhwgfK3X4Zp3IiSdsv/uEvhf8KohRfias3VvvFiboTXr7TNwhh.', role = 'ADMIN' where name = 'ADMIN';

--Пароль: test123
insert into intershop.users (name, password, role) values ('user1', '$2a$10$4MqhwgfK3X4Zp3IiSdsv/uEvhf8KohRfias3VvvFiboTXr7TNwhh.', 'CUSTOMER');
insert into intershop.users (name, password, role) values ('user2', '$2a$10$4MqhwgfK3X4Zp3IiSdsv/uEvhf8KohRfias3VvvFiboTXr7TNwhh.', 'CUSTOMER');
