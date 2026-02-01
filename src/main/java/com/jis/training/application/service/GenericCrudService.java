package com.jis.training.application.service;

import com.jis.training.domain.port.in.CrudUseCase;
import com.jis.training.domain.port.out.PersistencePort;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class GenericCrudService<T, ID> implements CrudUseCase<T, ID> {

    protected final PersistencePort<T, ID> persistencePort;

    @Override
    public List<T> getAll() {
        return persistencePort.findAll();
    }

    @Override
    public Optional<T> getById(ID id) {
        return persistencePort.findById(id);
    }

    @Override
    public T create(T entity) {
        return persistencePort.save(entity);
    }

    @Override
    public T update(ID id, T entity) {
        // ideally check if exists, but for simple crud:
        return persistencePort.save(entity);
    }

    @Override
    public void delete(ID id) {
        persistencePort.deleteById(id);
    }
}
