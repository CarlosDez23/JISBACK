package com.jis.training.application.service;

import com.jis.training.domain.model.Simulacro;
import com.jis.training.domain.port.out.PersistencePort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SimulacroService extends GenericCrudService<Simulacro, Long> {
    public SimulacroService(@Qualifier("simulacroPersistenceAdapter") PersistencePort<Simulacro, Long> persistencePort) {
        super(persistencePort);
    }
}
