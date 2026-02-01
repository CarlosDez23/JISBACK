package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.Comunidad;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.infrastructure.adapter.out.persistence.entity.ComunidadEntity;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.ComunidadEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.ComunidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ComunidadPersistenceAdapter implements PersistencePort<Comunidad, Integer> {

    private final ComunidadRepository repository;
    private final ComunidadEntityMapper mapper;

    @Override
    public List<Comunidad> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Comunidad> findById(Integer id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Comunidad save(Comunidad entity) {
        ComunidadEntity dbEntity = mapper.toEntity(entity);
        return mapper.toDomain(repository.save(dbEntity));
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
