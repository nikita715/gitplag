create table source_code (
  id serial primary key,
  p_user serial references p_user(id) not null,
  repo serial references repository(id) not null,
  file_name varchar(200) not null,
  file_text text not null,
  unique(p_user, repo, file_name)
);