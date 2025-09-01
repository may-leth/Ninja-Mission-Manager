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
INSERT INTO ninjas (id, name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES (7, 'Sakura Haruno', 'sakura@gmail.com', '$2a$10$rDJnvWiHFAZz1Rfucww8peM5WPRLuynB93ILWxGbPySYymtjfrt1m', 500, 'CHUNIN', false, 1);

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
INSERT INTO ninja_roles (ninja_id, roles) VALUES (7, 'ROLE_NINJA_USER');

INSERT INTO missions (id, title, description, reward, mission_difficulty, status, creation_date)
VALUES (1, 'Misión de Escolta en la Tierra de las Olas', 'Escoltar al constructor de puentes Tazuna a la Tierra de las Olas. Esta misión se complica rápidamente, pasando de un rango C a un nivel A.', 1000, 'C', 'COMPLETED', NOW());

INSERT INTO missions (id, title, description, reward, mission_difficulty, status, creation_date)
VALUES (2, 'Recuperación de la Princesa de la Nieve', 'El Equipo 7 es contratado para proteger a una actriz en el País de la Nieve, pero la misión oculta un conflicto político y una traición.', 2500, 'A', 'COMPLETED', NOW());

INSERT INTO missions (id, title, description, reward, mission_difficulty, status, creation_date)
VALUES (3, 'Búsqueda del Gato Perdido Tora', 'Una misión de rango D aparentemente simple: encontrar al gato perdido de Madame Shijimi. Es conocida por ser una de las misiones más exasperantes.', 100, 'D', 'COMPLETED', NOW());

INSERT INTO missions (id, title, description, reward, mission_difficulty, status, creation_date)
VALUES (4, 'El Ataque al Puente de Kannabi', 'Una misión de alto riesgo para el Equipo Minato. Consistía en destruir el puente de Kannabi para cortar las líneas de suministro de la Aldea Oculta de la Roca.', 10000, 'S', 'COMPLETED', NOW());

INSERT INTO missions (id, title, description, reward, mission_difficulty, status, creation_date)
VALUES (5, 'El Examen de los Cascabeles', 'Una prueba de supervivencia y trabajo en equipo impuesta por Kakashi Hatake a sus genin para evaluar sus habilidades y cooperación.', 50, 'D', 'COMPLETED', NOW());

INSERT INTO missions (id, title, description, reward, mission_difficulty, status, creation_date)
VALUES (6, 'Infiltración en la Aldea de la Lluvia', 'Jiraiya se infiltra en la Aldea de la Lluvia para investigar al líder de Akatsuki, Pain. Se considera una de las misiones más peligrosas de su carrera.', 50000, 'S', 'ACTIVE', NOW());

INSERT INTO missions (id, title, description, reward, mission_difficulty, status, creation_date)
VALUES (7, 'Asignar nuevas misiones', 'El Hokage debe asignar nuevas misiones a los equipos ninja en función de sus habilidades y disponibilidad.', 5, 'D', 'PENDING', NOW());

INSERT INTO ninja_missions (ninja_id, mission_id) VALUES (1, 1);
INSERT INTO ninja_missions (ninja_id, mission_id) VALUES (2, 1);
INSERT INTO ninja_missions (ninja_id, mission_id) VALUES (3, 1);
INSERT INTO ninja_missions (ninja_id, mission_id) VALUES (7, 1);

INSERT INTO ninja_missions (ninja_id, mission_id) VALUES (1, 2);
INSERT INTO ninja_missions (ninja_id, mission_id) VALUES (2, 2);
INSERT INTO ninja_missions (ninja_id, mission_id) VALUES (3, 2);
INSERT INTO ninja_missions (ninja_id, mission_id) VALUES (7, 2);

INSERT INTO ninja_missions (ninja_id, mission_id) VALUES (1, 3);
INSERT INTO ninja_missions (ninja_id, mission_id) VALUES (2, 3);
INSERT INTO ninja_missions (ninja_id, mission_id) VALUES (3, 3);
INSERT INTO ninja_missions (ninja_id, mission_id) VALUES (7, 3);

INSERT INTO ninja_missions (ninja_id, mission_id) VALUES (5, 5);