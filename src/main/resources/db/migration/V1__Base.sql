create table p_user (
  id serial primary key,
  name varchar(30) not null unique,
  email varchar(50) not null unique,
  pword varchar(200) not null
);