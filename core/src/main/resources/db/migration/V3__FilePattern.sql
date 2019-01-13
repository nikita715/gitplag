create table repository (
  id serial primary key,
  name varchar(200) not null,
  link varchar(200) not null unique,
  owner serial references p_user(id) not null
);

create table repository_pattern (
  repository serial references repository(id) not null,
  pattern varchar(200) not null
);