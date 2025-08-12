SET REFERENTIAL_INTEGRITY FALSE;

DELETE FROM ninja_roles;
DELETE FROM ninjas;
DELETE FROM villages;

SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO villages (id, name, kage_id) VALUES (1, 'Konoha', NULL);
INSERT INTO villages (id, name, kage_id) VALUES (2, 'Suna', NULL);
INSERT INTO villages (id, name, kage_id) VALUES (3, 'Kiri', NULL);
INSERT INTO villages (id, name, kage_id) VALUES (4, 'Kumo', NULL);
INSERT INTO villages (id, name, kage_id) VALUES (5, 'Iwa', NULL);

INSERT INTO ninjas (id, name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES (1, 'Naruto Uzumaki', 'naruto@konoha.com', '{bcrypt}$2a$10$TjGgVv3jJ.uR4wL.5c.S0O.i/G5.G5.G5.G5.G5.G5', 500, 'GENIN', false, 1);
INSERT INTO ninjas (id, name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES (2, 'Sasuke Uchiha', 'sasuke@konoha.com', '{bcrypt}$2a$10$TjGgVv3jJ.uR4wL.5c.S0O.i/G5.G5.G5.G5.G5.G5', 400, 'GENIN', false, 1);
INSERT INTO ninjas (id, name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES (3, 'Kakashi Hatake', 'kakashi@konoha.com', '{bcrypt}$2a$10$TjGgVv3jJ.uR4wL.5c.S0O.i/G5.G5.G5.G5.G5.G5', 1000, 'JONIN', true, 1);
INSERT INTO ninjas (id, name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES (4, 'Gaara', 'gaara@suna.com', '{bcrypt}$2a$10$TjGgVv3jJ.uR4wL.5c.S0O.i/G5.G5.G5.G5.G5.G5', 600, 'KAGE', false, 2);
INSERT INTO ninjas (id, name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES (5, 'Tsunade', 'tsunade@konoha.com', '{bcrypt}$2a$10$TjGgVv3jJ.uR4wL.5c.S0O.i/G5.G5.G5.G5.G5.G5', 1500, 'KAGE', false, 1);
INSERT INTO ninjas (id, name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES (6, 'Itachi Uchiha', 'itachi@anbu.com', '{bcrypt}$2a$10$TjGgVv3jJ.uR4wL.5c.S0O.i/G5.G5.G5.G5.G5.G5', 2000, 'JONIN', true, 1);

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