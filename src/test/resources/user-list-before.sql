delete from user_role;
delete from person;

insert into person(id, username, password)
values (1, 'admin', '$2a$10$pcKUsFZxvgAkDLhwmqHO8u/EMjhuGP4uM.kokRThk.n/2453ijVSu'),
(2, 'user', '$2a$10$IwwoqeJN2LtA6QZsssSZJei/YUU.CiYKQJrHSxYH8nKDycpnrkq7S');

insert into user_role (user_id, roles) values
(1, 'ADMIN'), (1, 'USER'),
(2, 'USER');

