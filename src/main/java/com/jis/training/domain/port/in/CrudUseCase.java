package com.jis.training.domain.port.in;

import java.util.List;
import java.util.Optional;

public interface CrudUseCase<T, ID> {
    List<T> getAll();

    Optional<T> getById(ID id);

    T create(T entity);

    T update(ID id, T entity);

    void delete(ID id);
}
