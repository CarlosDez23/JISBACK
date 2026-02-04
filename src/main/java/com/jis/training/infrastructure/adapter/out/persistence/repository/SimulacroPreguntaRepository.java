package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.SimulacroPreguntaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimulacroPreguntaRepository extends JpaRepository<SimulacroPreguntaEntity, Long> {
    List<SimulacroPreguntaEntity> findBySimulacroId(Long simulacroId);
    long countBySimulacroId(Long simulacroId);
    void deleteBySimulacroIdAndPreguntaId(Long simulacroId, Long preguntaId);
    void deleteBySimulacroId(Long simulacroId);
}
