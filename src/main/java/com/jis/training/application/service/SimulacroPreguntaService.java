package com.jis.training.application.service;

import com.jis.training.domain.model.SimulacroPregunta;
import com.jis.training.domain.port.out.PersistencePort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SimulacroPreguntaService extends GenericCrudService<SimulacroPregunta, Long> {
    public SimulacroPreguntaService(@Qualifier("simulacroPreguntaPersistenceAdapter") PersistencePort<SimulacroPregunta, Long> persistencePort) {
        super(persistencePort);
    }
}
