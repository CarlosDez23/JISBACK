package com.jis.training.application.service;

import com.jis.training.domain.model.Materia;
import com.jis.training.domain.port.out.PersistencePort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MateriaService extends GenericCrudService<Materia, Integer> {
    public MateriaService(@Qualifier("materiaPersistenceAdapter") PersistencePort<Materia, Integer> persistencePort) {
        super(persistencePort);
    }
}
