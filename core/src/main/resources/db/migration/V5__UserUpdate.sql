alter table p_user add column installation_id serial not null unique;
alter table p_user add column github_id serial not null unique;
alter table p_user drop column email;
alter table p_user drop column pword;