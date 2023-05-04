create table message (
    id serial not null,
    filename varchar(255),
    tag varchar(255),
    text varchar(2048),
    person_id integer,
    primary key (id)
);

create table person (
    id serial not null,
    activation_code varchar(255),
    email varchar(255),
    password varchar(255),
    username varchar(255),
    primary key (id)
);

create table user_role (
    user_id integer not null,
    roles varchar(255)
);

create table user_subscriptions (
    subscriber_id integer not null,
    channel_id integer not null,
    primary key (subscriber_id, channel_id)
);

alter table if exists message add constraint FKrjg2ug55rdo338ks6514fw9qy foreign key (person_id) references person;
alter table if exists user_role add constraint FKs92q0x8xfwil0ac1k3ucsnr93 foreign key (user_id) references person;
alter table if exists user_subscriptions add constraint FKmnev58ng7sh12r27qgil73228 foreign key (channel_id) references person;
alter table if exists user_subscriptions add constraint FKb17cbmgr57u9rulc3ciji5291 foreign key (subscriber_id) references person;