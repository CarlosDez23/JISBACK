package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.Simulacro;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.infrastructure.adapter.out.persistence.entity.SimulacroEntity;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.SimulacroEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.SimulacroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SimulacroPersistenceAdapter implements PersistencePort<Simulacro, Long> {

    private final SimulacroRepository repository;
    private final SimulacroEntityMapper mapper;

    @Override
    public List<Simulacro> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Simulacro> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Simulacro save(Simulacro entity) {
        SimulacroEntity dbEntity = mapper.toEntity(entity);
        return mapper.toDomain(repository.save(dbEntity));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
