SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS ninja_missions;
DROP TABLE IF EXISTS ninja_roles;
DROP TABLE IF EXISTS missions;
DROP TABLE IF EXISTS ninjas;
DROP TABLE IF EXISTS villages;

CREATE TABLE villages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    kage_id BIGINT DEFAULT NULL
);

CREATE TABLE ninjas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    missions_completed_count INT NOT NULL DEFAULT 0,
    ninja_rank VARCHAR(20),
    is_anbu BOOLEAN DEFAULT FALSE,
    village_id BIGINT,
    FOREIGN KEY (village_id) REFERENCES villages(id) ON DELETE SET NULL
);

ALTER TABLE villages
ADD CONSTRAINT fk_villages_kage
FOREIGN KEY (kage_id) REFERENCES ninjas(id) ON DELETE SET NULL;

CREATE TABLE ninja_roles (
    ninja_id BIGINT NOT NULL,
    roles VARCHAR(50) NOT NULL,
    PRIMARY KEY (ninja_id, roles),
    FOREIGN KEY (ninja_id) REFERENCES ninjas(id) ON DELETE CASCADE
);

CREATE TABLE missions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    reward INT DEFAULT 0,
    mission_difficulty VARCHAR(5),
    status VARCHAR(20) DEFAULT 'PENDING',
    creation_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ninja_missions (
    ninja_id BIGINT NOT NULL,
    mission_id BIGINT NOT NULL,
    PRIMARY KEY (ninja_id, mission_id),
    FOREIGN KEY (ninja_id) REFERENCES ninjas(id) ON DELETE CASCADE,
    FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE CASCADE
);