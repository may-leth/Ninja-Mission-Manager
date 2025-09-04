INSERT INTO villages (name) VALUES
('Konoha'),
('Suna'),
('Kiri'),
('Kumo'),
('Iwa');

INSERT INTO ninjas (name, email, password, missions_completed_count, ninja_rank, is_anbu, village_id) VALUES
('Naruto Uzumaki', 'naruto@gmail.com', '$2a$10$qPgLBWxglYkyehl9Ou.IsuSw7/u..vBDGwEKqRc6FP9n/S11mwcWa', 500, 'GENIN', false, 1),
('Sasuke Uchiha', 'sasuke@gmail.com', '$2a$10$yM7bKzGUYSCCUVDclILqgOzLKPaJE3hMKawDeKklgW8.2zqZr34Qi', 400, 'GENIN', false, 1),
('Sakura Haruno', 'sakura@gmail.com', '$2a$10$rDJnvWiHFAZz1Rfucww8peM5WPRLuynB93ILWxGbPySYymtjfrt1m', 500, 'CHUNIN', false, 1),
('Kakashi Hatake', 'kakashi@gmail.com', '$2a$10$K5QRPwpvDAhySoledpd0RO6P/7x7Aq5mw27WmYxZmCqLfGUQli69.', 1000, 'JONIN', true, 1),
('Gaara', 'gaara@gmail.com', '$2a$10$UuYyU6d0DXC0jsP1H/sEeebSVcrRek2saOF11GO12RqFMG6XnQR0G', 600, 'KAGE', false, 2),
('Tsunade', 'tsunade@gmail.com', '$2a$10$PaothPnYKjsDL/g9VoRB5OKcd8DGaKC8RirmTHqe0t5hjsSV0yaV.', 1500, 'KAGE', false, 1),
('Itachi Uchiha', 'itachi@gmail.com', '$2a$10$cHRONQT1bOveD7hM1FwUJOIxvvKRisIyksrjzf0mxQ3nTJU0Shoya', 2000, 'JONIN', true, 1);

UPDATE villages SET kage_id = 6 WHERE name = 'Konoha';
UPDATE villages SET kage_id = 5 WHERE name = 'Suna';

INSERT INTO ninja_roles (ninja_id, roles) VALUES
(1, 'ROLE_NINJA_USER'),
(2, 'ROLE_NINJA_USER'),
(3, 'ROLE_NINJA_USER'),
(4, 'ROLE_NINJA_USER'),
(4, 'ROLE_ANBU'),
(5, 'ROLE_KAGE'),
(6, 'ROLE_KAGE'),
(7, 'ROLE_NINJA_USER'),
(7, 'ROLE_ANBU');

INSERT INTO missions (title, description, reward, mission_difficulty, status, creation_date) VALUES
('Misión de Escolta en la Tierra de las Olas', 'Escoltar al constructor de puentes Tazuna a la Tierra de las Olas. Esta misión se complica rápidamente, pasando de un rango C a un nivel A.', 1000, 'C', 'COMPLETED', NOW()),
('Recuperación de la Princesa de la Nieve', 'El Equipo 7 es contratado para proteger a una actriz en el País de la Nieve, pero la misión oculta un conflicto político y una traición.', 2500, 'A', 'COMPLETED', NOW()),
('Búsqueda del Gato Perdido Tora', 'Una misión de rango D aparentemente simple: encontrar al gato perdido de Madame Shijimi. Es conocida por ser una de las misiones más exasperantes.', 100, 'D', 'COMPLETED', NOW()),
('El Ataque al Puente de Kannabi', 'Una misión de alto riesgo para el Equipo Minato. Consistía en destruir el puente de Kannabi para cortar las líneas de suministro de la Aldea Oculta de la Roca.', 10000, 'S', 'COMPLETED', NOW()),
('El Examen de los Cascabeles', 'Una prueba de supervivencia y trabajo en equipo impuesta por Kakashi Hatake a sus genin para evaluar sus habilidades y cooperación.', 50, 'D', 'COMPLETED', NOW()),
('Infiltración en la Aldea de la Lluvia', 'Jiraiya se infiltra en la Aldea de la Lluvia para investigar al líder de Akatsuki, Pain. Se considera una de las misiones más peligrosas de su carrera.', 50000, 'S', 'ACTIVE', NOW()),
('Asignar nuevas misiones', 'El Hokage debe asignar nuevas misiones a los equipos ninja en función de sus habilidades y disponibilidad.', 5, 'D', 'PENDING', NOW());

INSERT INTO ninja_missions (ninja_id, mission_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(1, 2),
(2, 2),
(3, 2),
(4, 2),
(1, 3),
(2, 3),
(3, 3),
(4, 3),
(5, 5);

SET FOREIGN_KEY_CHECKS = 1;