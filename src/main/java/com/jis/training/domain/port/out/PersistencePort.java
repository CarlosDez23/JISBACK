package com.jis.training.domain.port.out;

import java.util.List;
import java.util.Optional;

public interface PersistencePort<T, ID> {
    List<T> findAll();

    Optional<T> findById(ID id);

    T save(T entity);

    void deleteById(ID id);
}
