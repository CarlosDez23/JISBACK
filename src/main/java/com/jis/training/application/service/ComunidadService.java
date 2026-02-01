package com.jis.training.application.service;

import com.jis.training.domain.model.Comunidad;
import com.jis.training.domain.port.out.PersistencePort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ComunidadService extends GenericCrudService<Comunidad, Integer> {
    public ComunidadService(
            @Qualifier("comunidadPersistenceAdapter") PersistencePort<Comunidad, Integer> persistencePort) {
        super(persistencePort);
    }
}
