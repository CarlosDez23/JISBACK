package com.jis.training.domain.port.out;

import com.jis.training.domain.model.SimulacroPregunta;

import java.util.List;

public interface SimulacroPreguntaRepositoryPort {
    List<SimulacroPregunta> findBySimulacroId(Long simulacroId);
    long countBySimulacroId(Long simulacroId);
    void deleteBySimulacroIdAndPreguntaId(Long simulacroId, Long preguntaId);
    void deleteBySimulacroId(Long simulacroId);
}
