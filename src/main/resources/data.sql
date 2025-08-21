INSERT INTO villages (id, name, kage_id) VALUES (1, 'Konoha', NULL);
INSERT INTO villages (id, name, kage_id) VALUES (2, 'Suna', NULL);
INSERT INTO villages (id, name, kage_id) VALUES (3, 'Kiri', NULL);
INSERT INTO villages (id, name, kage_id) VALUES (4, 'Kumo', NULL);
INSERT INTO villages (id, name, kage_id) VALUES (5, 'Iwa', NULL);

INSERT INTO ninjas (id, name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES (1, 'Naruto Uzumaki', 'naruto@gmail.com', '$2a$10$qPgLBWxglYkyehl9Ou.IsuSw7/u..vBDGwEKqRc6FP9n/S11mwcWa', 500, 'GENIN', false, 1);
INSERT INTO ninjas (id, name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES (2, 'Sasuke Uchiha', 'sasuke@gmail.com', '$2a$10$yM7bKzGUYSCCUVDclILqgOzLKPaJE3hMKawDeKklgW8.2zqZr34Qi', 400, 'GENIN', false, 1);
INSERT INTO ninjas (id, name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES (3, 'Kakashi Hatake', 'kakashi@gmail.com', '$2a$10$K5QRPwpvDAhySoledpd0RO6P/7x7Aq5mw27WmYxZmCqLfGUQli69.', 1000, 'JONIN', true, 1);
INSERT INTO ninjas (id, name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES (4, 'Gaara', 'gaara@gmail.com', '$2a$10$UuYyU6d0DXC0jsP1H/sEeebSVcrRek2saOF11GO12RqFMG6XnQR0G', 600, 'KAGE', false, 2);
INSERT INTO ninjas (id, name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES (5, 'Tsunade', 'tsunade@gmail.com', '$2a$10$PaothPnYKjsDL/g9VoRB5OKcd8DGaKC8RirmTHqe0t5hjsSV0yaV.', 1500, 'KAGE', false, 1);
INSERT INTO ninjas (id, name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES (6, 'Itachi Uchiha', 'itachi@gmail.com', '$2a$10$cHRONQT1bOveD7hM1FwUJOIxvvKRisIyksrjzf0mxQ3nTJU0Shoya', 2000, 'JONIN', true, 1);

UPDATE villages SET kage_id = 5 WHERE id = 1;
UPDATE villages SET kage_id = 4 WHERE id = 2;

INSERT INTO ninja_roles (ninja_id, roles) VALUES (1, 'ROLE_NINJA_USER');
INSERT INTO ninja_roles (ninja_id, roles) VALUES (2, 'ROLE_NINJA_USER');
INSERT INTO ninja_roles (ninja_id, roles) VALUES (3, 'ROLE_NINJA_USER');
INSERT INTO ninja_roles (ninja_id, roles) VALUES (3, 'ROLE_ANBU');
INSERT INTO ninja_roles (ninja_id, roles) VALUES (4, 'ROLE_NINJA_USER');
INSERT INTO ninja_roles (ninja_id, roles) VALUES (4, 'ROLE_KAGE');
INSERT INTO ninja_roles (ninja_id, roles) VALUES (5, 'ROLE_NINJA_USER');
INSERT INTO ninja_roles (ninja_id, roles) VALUES (5, 'ROLE_KAGE');
INSERT INTO ninja_roles (ninja_id, roles) VALUES (6, 'ROLE_NINJA_USER');
INSERT INTO ninja_roles (ninja_id, roles) VALUES (6, 'ROLE_ANBU');