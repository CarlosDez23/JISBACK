package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.SimulacroPregunta;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.infrastructure.adapter.out.persistence.entity.SimulacroPreguntaEntity;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.SimulacroPreguntaEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.SimulacroPreguntaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SimulacroPreguntaPersistenceAdapter implements PersistencePort<SimulacroPregunta, Long> {

    private final SimulacroPreguntaRepository repository;
    private final SimulacroPreguntaEntityMapper mapper;

    @Override
    public List<SimulacroPregunta> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SimulacroPregunta> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public SimulacroPregunta save(SimulacroPregunta entity) {
        SimulacroPreguntaEntity dbEntity = mapper.toEntity(entity);
        return mapper.toDomain(repository.save(dbEntity));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
