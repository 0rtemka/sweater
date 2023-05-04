delete from message;

insert into message(id, text, tag, person_id) values
(1, 'first', '1', 1),
(2, 'second', '1', 1),
(3, 'third', '2', 2);

alter sequence message_id_seq restart with 10;