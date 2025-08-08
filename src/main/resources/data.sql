INSERT INTO ninja (id, name, email, encrypted_password, missions_completed_count, rank, is_anbu, village_id)
VALUES (1, 'Naruto Uzumaki', 'naruto@konoha.com', '{bcrypt}$2a$10$TjGgVv3jJ.uR4wL.5c.S0O.i/G5.G5.G5.G5.G5.G5', 500, 'GENIN', FALSE, 1);

INSERT INTO ninja (id, name, email, encrypted_password, missions_completed_count, rank, is_anbu, village_id)
VALUES (2, 'Sasuke Uchiha', 'sasuke@konoha.com', '{bcrypt}$2a$10$TjGgVv3jJ.uR4wL.5c.S0O.i/G5.G5.G5.G5.G5.G5', 400, 'GENIN', FALSE, 1);

INSERT INTO ninja (id, name, email, encrypted_password, missions_completed_count, rank, is_anbu, village_id)
VALUES (3, 'Kakashi Hatake', 'kakashi@konoha.com', '{bcrypt}$2a$10$TjGgVv3jJ.uR4wL.5c.S0O.i/G5.G5.G5.G5.G5.G5', 1000, 'JONIN', TRUE, 1);

INSERT INTO ninja (id, name, email, encrypted_password, missions_completed_count, rank, is_anbu, village_id)
VALUES (4, 'Gaara', 'gaara@suna.com', '{bcrypt}$2a$10$TjGgVv3jJ.uR4wL.5c.S0O.i/G5.G5.G5.G5.G5.G5', 600, 'GENIN', FALSE, 2);

INSERT INTO ninja (id, name, email, encrypted_password, missions_completed_count, rank, is_anbu, village_id)
VALUES (5, 'Tsunade', 'tsunade@konoha.com', '{bcrypt}$2a$10$TjGgVv3jJ.uR4wL.5c.S0O.i/G5.G5.G5.G5.G5.G5', 1500, 'KAGE', FALSE, 1);

INSERT INTO villages (id, name, kage_id) VALUES (1, 'Konoha', 5);
INSERT INTO villages (id, name, kage_id) VALUES (2, 'Suna', 4);
INSERT INTO villages (id, name, kage_id) VALUES (3, 'Kiri', NULL);
INSERT INTO villages (id, name, kage_id) VALUES (4, 'Kumo', NULL);
INSERT INTO villages (id, name, kage_id) VALUES (5, 'Iwa', NULL);