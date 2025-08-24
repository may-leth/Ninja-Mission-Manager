SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS ninja_missions;
DROP TABLE IF EXISTS ninja_roles;
DROP TABLE IF EXISTS ninjas;
DROP TABLE IF EXISTS missions;
DROP TABLE IF EXISTS villages;

CREATE TABLE villages (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    kage_id BIGINT,
    FOREIGN KEY (kage_id) REFERENCES ninjas(id)
);

CREATE TABLE ninjas (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    missions_completed_count INT NOT NULL,
    ninja_rank VARCHAR(20),
    is_anbu BOOLEAN,
    village_id BIGINT,
    FOREIGN KEY (village_id) REFERENCES villages(id)
);

CREATE TABLE ninja_roles (
    ninja_id BIGINT NOT NULL,
    roles VARCHAR(50) NOT NULL,
    PRIMARY KEY (ninja_id, roles),
    FOREIGN KEY (ninja_id) REFERENCES ninjas(id)
);

CREATE TABLE missions (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    reward INT,
    mission_difficulty VARCHAR(5),
    status VARCHAR(20),
    creation_date DATETIME
);

CREATE TABLE ninja_missions (
    ninja_id BIGINT NOT NULL,
    mission_id BIGINT NOT NULL,
    PRIMARY KEY (ninja_id, mission_id),
    FOREIGN KEY (ninja_id) REFERENCES ninjas(id),
    FOREIGN KEY (mission_id) REFERENCES missions(id)
);

SET FOREIGN_KEY_CHECKS = 1;