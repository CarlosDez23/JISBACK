-- Crear un esquema llamado 'jis_training'
CREATE SCHEMA IF NOT EXISTS jis_training;

drop table if exists jis_training.answers;
drop table if exists jis_training.questions;
drop table if exists jis_training.topics;
drop table if exists jis_trainig.materia;
drop table if exists jis_training.comunidad;
drop table if exists jis_training.users;


CREATE TABLE jis_training.comunidad (
    id INT generated always AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,A
    grupo_oposicion VARCHAR(255) NOT NULL
);

INSERT INTO jis_training.comunidad (nombre, grupo_oposicion) VALUES
    ('ANDALUCÍA', 'A4-FARMACIA'),
    ('ARAGÓN', 'FARMACÉUTICOS ADMINISTRACIÓN SANITARIA'),
    ('CASTILLA-LA MANCHA', 'SANITARIOS LOCALES Y CUERPO SUPERIOR'),
    ('EXTREMADURA', 'FARMACÉUTICOS EAP'),
    ('MADRID', 'TÉCNICOS SALUD PÚBLICA FARMACIA'),
    ('MURCIA', 'FARMACEÚTICOS DE SALUD PÚBLICA'),
    ('COMUNITAT VALENCIANA', 'FARMACÉUTICOS'),
    ('COMUNITAT VALENCIANA', 'TÉCNICOS SEGURIDAD ALIMENTARIA'),
    ('COMUNITAT VALENCIANA', 'TÉCNICOS SANIDAD AMBIENTAL');

CREATE TABLE jis_training.materia (
    id INT GENERATED always AS IDENTITY PRIMARY KEY,
    nombre_materia VARCHAR(150) NOT NULL,
    sigla VARCHAR(50) NOT NULL,
    comunidad INT NOT NULL, 
    
    CONSTRAINT fk_materia_comunidad 
        FOREIGN KEY (comunidad) 
        REFERENCES jis_training.comunidad(id)
        ON DELETE CASCADE 
);

INSERT INTO jis_training.materia (nombre_materia, sigla, comunidad) VALUES
    ('COMUN', 'CLM PC', 3),
    ('SEGURIDAD ALIMENTARIA', 'CLM SA', 3),
    ('SANIDAD AMBIENTAL', 'CLM SAM', 3),
    ('SALUD PUBLICA', 'CLM SP', 3),
    ('ORDENACION FARMACÉUTICA', 'CLM MED', 3);
   


CREATE TABLE jis_training.topics (
    id bigint primary key generated always as identity,
    topic_name text not null, 
    materia int not null references jis_training.materia(id)
);

COMMENT ON TABLE jis_training.topics IS 'Tabla para almacenar los temas';
COMMENT ON COLUMN jis_training.topics.id IS 'Identificador único del tema';
COMMENT ON COLUMN jis_training.topics.topic_name IS 'Nombre del tema';


CREATE TABLE jis_training.questions (
    id bigint primary key generated always as identity,
    question_text text not null,
    topic_id bigint references jis_training.topics(id)
);

COMMENT ON TABLE jis_training.questions IS 'Tabla para almacenar las preguntas';
COMMENT ON COLUMN jis_training.questions.id IS 'Identificador único de la pregunta';
COMMENT ON COLUMN jis_training.questions.question_text IS 'Texto de la pregunta';
COMMENT ON COLUMN jis_training.questions.topic_id IS 'Referencia al tema al que pertenece la pregunta';


CREATE TABLE jis_training.answers (
    id bigint primary key generated always as identity,
    question_id bigint references jis_training.questions(id) on delete cascade,
    answer_text text not null,
    is_correct boolean not null,
    explanation text
);

COMMENT ON TABLE jis_training.answers IS 'Tabla para almacenar las respuestas';
COMMENT ON COLUMN jis_training.answers.id IS 'Identificador único de la respuesta';
COMMENT ON COLUMN jis_training.answers.question_id IS 'Referencia a la pregunta correspondiente';
COMMENT ON COLUMN jis_training.answers.answer_text IS 'Texto de la respuesta';
COMMENT ON COLUMN jis_training.answers.is_correct IS 'Indica si la respuesta es correcta';
COMMENT ON COLUMN jis_training.answers.explanation IS 'Explicación de por qué la respuesta es correcta (si aplica)';

-- Entity User added as requested
CREATE TABLE jis_training.users (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

INSERT INTO jis_training.users (username, password) VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'); -- password: password
