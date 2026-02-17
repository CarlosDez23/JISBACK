package com.jis.training.application.service;

import com.jis.training.domain.model.Materia;
import com.jis.training.infrastructure.adapter.out.persistence.MateriaPersistenceAdapter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MateriaService extends GenericCrudService<Materia, Integer> {

    private final MateriaPersistenceAdapter materiaPersistenceAdapter;

    public MateriaService(MateriaPersistenceAdapter materiaPersistenceAdapter) {
        super(materiaPersistenceAdapter);
        this.materiaPersistenceAdapter = materiaPersistenceAdapter;
    }

    public List<Materia> findByComunidadId(Integer comunidadId) {
        return materiaPersistenceAdapter.findByComunidadId(comunidadId);
    }
}
