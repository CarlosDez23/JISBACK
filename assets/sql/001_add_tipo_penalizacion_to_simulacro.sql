-- =============================================================================
-- File: 001_add_tipo_penalizacion_to_simulacro.sql
-- Description: Adds tipo_penalizacion column to simulacro table and provides
--              the updated full table definition for schema.sql reference.
-- Author: postgres-data-architect
-- Created: 2026-02-19
-- =============================================================================


-- =============================================================================
-- PART 1: MIGRATION (ejecutar en produccion)
-- =============================================================================
-- Adds the new column as nullable to maintain backward compatibility with
-- existing rows. A CHECK constraint restricts values to the two Java enum
-- entries: 'TRES_A_UNO' and 'CUATRO_A_UNO'. NULL is explicitly permitted
-- so that rows inserted before this migration remain valid.
-- =============================================================================

ALTER TABLE jis_training.simulacro
    ADD COLUMN tipo_penalizacion VARCHAR(15) NULL
        CONSTRAINT chk_simulacro_tipo_penalizacion
            CHECK (tipo_penalizacion IN ('TRES_A_UNO', 'CUATRO_A_UNO'));

COMMENT ON COLUMN jis_training.simulacro.tipo_penalizacion
    IS 'Tipo de penalizacion del simulacro. Valores admitidos: TRES_A_UNO (penaliza 1 por cada 3 incorrectas), CUATRO_A_UNO (penaliza 1 por cada 4 incorrectas). Nullable para registros historicos.';


-- =============================================================================
-- PART 2: DEFINICION COMPLETA DE LA TABLA (para schema.sql de referencia)
-- =============================================================================
-- Esta seccion NO debe ejecutarse en produccion. Es la definicion completa
-- de la tabla simulacro con la nueva columna incluida, para mantener
-- actualizado el schema.sql de referencia del proyecto.
-- =============================================================================

/*
CREATE TABLE jis_training.simulacro (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nonmbre_simulacro VARCHAR(255) NOT NULL,
    comunidad   INT,
    materia     INT,
    tiempo_limite_segundos INT,
    tipo_penalizacion VARCHAR(15) NULL,

    CONSTRAINT fk_simulacro_comunidad
        FOREIGN KEY (comunidad)
        REFERENCES jis_training.comunidad(id),

    CONSTRAINT fk_simulacro_materia
        FOREIGN KEY (materia)
        REFERENCES jis_training.materia(id),

    CONSTRAINT chk_simulacro_tipo_penalizacion
        CHECK (tipo_penalizacion IN ('TRES_A_UNO', 'CUATRO_A_UNO'))
);

COMMENT ON TABLE jis_training.simulacro
    IS 'Tabla para almacenar los simulacros de examen';
COMMENT ON COLUMN jis_training.simulacro.tipo_penalizacion
    IS 'Tipo de penalizacion del simulacro. Valores admitidos: TRES_A_UNO (penaliza 1 por cada 3 incorrectas), CUATRO_A_UNO (penaliza 1 por cada 4 incorrectas). Nullable para registros historicos.';
*/


-- =============================================================================
-- ROLLBACK (en caso de necesidad)
-- =============================================================================
-- ALTER TABLE jis_training.simulacro DROP COLUMN tipo_penalizacion;
-- =============================================================================
