insert into person (username, password)
values ('admin', '$2a$10$5DlfDEx2KJjYetYL2uqqEuUMkIJRpOfBUsKpNwzI7MB9FnSgvmeae');

insert into user_role (user_id, roles)
values (1, 'USER'), (1, 'ADMIN');