create table pull_request (
  id serial primary key,
  author varchar(50) not null,
  link varchar(200) not null
);