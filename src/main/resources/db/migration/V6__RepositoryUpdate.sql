alter table repository add column github_id serial not null unique;
alter table repository drop column link;