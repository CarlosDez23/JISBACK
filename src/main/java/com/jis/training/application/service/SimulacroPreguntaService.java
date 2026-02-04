package com.jis.training.application.service;

import com.jis.training.domain.model.SimulacroPregunta;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.SimulacroPreguntaRepositoryPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SimulacroPreguntaService extends GenericCrudService<SimulacroPregunta, Long> {

    private final SimulacroPreguntaRepositoryPort simulacroPreguntaRepositoryPort;

    public SimulacroPreguntaService(
            @Qualifier("simulacroPreguntaPersistenceAdapter") PersistencePort<SimulacroPregunta, Long> persistencePort,
            SimulacroPreguntaRepositoryPort simulacroPreguntaRepositoryPort) {
        super(persistencePort);
        this.simulacroPreguntaRepositoryPort = simulacroPreguntaRepositoryPort;
    }

    public List<SimulacroPregunta> findBySimulacroId(Long simulacroId) {
        return simulacroPreguntaRepositoryPort.findBySimulacroId(simulacroId);
    }

    public long countBySimulacroId(Long simulacroId) {
        return simulacroPreguntaRepositoryPort.countBySimulacroId(simulacroId);
    }

    @Transactional
    public void deleteBySimulacroIdAndPreguntaId(Long simulacroId, Long preguntaId) {
        simulacroPreguntaRepositoryPort.deleteBySimulacroIdAndPreguntaId(simulacroId, preguntaId);
    }

    @Transactional
    public void deleteBySimulacroId(Long simulacroId) {
        simulacroPreguntaRepositoryPort.deleteBySimulacroId(simulacroId);
    }
}
