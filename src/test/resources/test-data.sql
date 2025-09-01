SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE ninja_missions;
TRUNCATE TABLE ninja_roles;
TRUNCATE TABLE missions;
TRUNCATE TABLE ninjas;
TRUNCATE TABLE villages;

ALTER TABLE ninja_missions ALTER COLUMN ninja_id RESTART WITH 1;
ALTER TABLE ninja_missions ALTER COLUMN mission_id RESTART WITH 1;
ALTER TABLE ninja_roles ALTER COLUMN ninja_id RESTART WITH 1;
ALTER TABLE missions ALTER COLUMN id RESTART WITH 1;
ALTER TABLE ninjas ALTER COLUMN id RESTART WITH 1;
ALTER TABLE villages ALTER COLUMN id RESTART WITH 1;

SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO villages (name, kage_id) VALUES
('Konoha', NULL),
('Suna', NULL),
('Kiri', NULL),
('Kumo', NULL),
('Iwa', NULL);

INSERT INTO ninjas (name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES
('Naruto Uzumaki', 'naruto@gmail.com', '$2a$10$qPgLBWxglYkyehl9Ou.IsuSw7/u..vBDGwEKqRc6FP9n/S11mwcWa', 500, 'GENIN', false, 1),
('Sasuke Uchiha', 'sasuke@gmail.com', '$2a$10$yM7bKzGUYSCCUVDclILqgOzLKPaJE3hMKawDeKklgW8.2zqZr34Qi', 400, 'GENIN', false, 1),
('Kakashi Hatake', 'kakashi@gmail.com', '$2a$10$K5QRPwpvDAhySoledpd0RO6P/7x7Aq5mw27WmYxZmCqLfGUQli69.', 1000, 'JONIN', true, 1),
('Gaara', 'gaara@gmail.com', '$2a$10$UuYyU6d0DXC0jsP1H/sEeebSVcrRek2saOF11GO12RqFMG6XnQR0G', 600, 'KAGE', false, 2),
('Tsunade', 'tsunade@gmail.com', '$2a$10$PaothPnYKjsDL/g9VoRB5OKcd8DGaKC8RirmTHqe0t5hjsSV0yaV.', 1500, 'KAGE', false, 1),
('Itachi Uchiha', 'itachi@gmail.com', '$2a$10$cHRONQT1bOveD7hM1FwUJOIxvvKRisIyksrjzf0mxQ3nTJU0Shoya', 2000, 'JONIN', true, 1),
('Sakura Haruno', 'sakura@gmail.com', '$2a$10$rDJnvWiHFAZz1Rfucww8peM5WPRLuynB93ILWxGbPySYymtjfrt1m', 500, 'CHUNIN', false, 1);

UPDATE villages SET kage_id = (SELECT id FROM ninjas WHERE name = 'Tsunade') WHERE name = 'Konoha';
UPDATE villages SET kage_id = (SELECT id FROM ninjas WHERE name = 'Gaara') WHERE name = 'Suna';

INSERT INTO ninja_roles (ninja_id, roles) VALUES
(1, 'ROLE_NINJA_USER'),
(2, 'ROLE_NINJA_USER'),
(3, 'ROLE_NINJA_USER'),
(3, 'ROLE_ANBU'),
(4, 'ROLE_NINJA_USER'),
(4, 'ROLE_KAGE'),
(5, 'ROLE_NINJA_USER'),
(5, 'ROLE_KAGE'),
(6, 'ROLE_NINJA_USER'),
(6, 'ROLE_ANBU'),
(7, 'ROLE_NINJA_USER');

INSERT INTO missions (title, description, reward, mission_difficulty, status, creation_date) VALUES
('Misión de Escolta en la Tierra de las Olas', 'Escoltar al constructor de puentes Tazuna a la Tierra de las Olas...', 1000, 'C', 'COMPLETED', NOW()),
('Recuperación de la Princesa de la Nieve', 'El Equipo 7 es contratado...', 2500, 'A', 'COMPLETED', NOW()),
('Búsqueda del Gato Perdido Tora', 'Una misión de rango D...', 100, 'D', 'COMPLETED', NOW()),
('El Ataque al Puente de Kannabi', 'Una misión de alto riesgo...', 10000, 'S', 'COMPLETED', NOW()),
('El Examen de los Cascabeles', 'Una prueba de supervivencia...', 50, 'D', 'COMPLETED', NOW()),
('Infiltración en la Aldea de la Lluvia', 'Jiraiya se infiltra...', 50000, 'S', 'ACTIVE', NOW()),
('Asignar nuevas misiones', 'El Hokage debe asignar nuevas misiones...', 5, 'D', 'PENDING', NOW());

INSERT INTO ninja_missions (ninja_id, mission_id) VALUES
(1, 1),(2, 1),(3, 1),(7, 1),
(1, 2),(2, 2),(3, 2),(7, 2),
(1, 3),(2, 3),(3, 3),(7, 3),
(5, 5);
