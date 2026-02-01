package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.Materia;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.infrastructure.adapter.out.persistence.entity.MateriaEntity;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.MateriaEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MateriaPersistenceAdapter implements PersistencePort<Materia, Integer> {

    private final MateriaRepository repository;
    private final MateriaEntityMapper mapper;

    @Override
    public List<Materia> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Materia> findById(Integer id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Materia save(Materia entity) {
        MateriaEntity dbEntity = mapper.toEntity(entity);
        return mapper.toDomain(repository.save(dbEntity));
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
