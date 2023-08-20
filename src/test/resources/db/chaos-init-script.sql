create schema test;
set schema 'test';

create table if not exists test.user (
     id bigserial not null,
     name varchar not null,
     age  int not null,
     email varchar not null,
     primary key (id),
UNIQUE (email)
);