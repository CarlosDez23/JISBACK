package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.Aviso;
import com.jis.training.domain.port.out.AvisoRepositoryPort;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.infrastructure.adapter.out.persistence.entity.AvisoEntity;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.AvisoEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.AvisoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("avisoPersistenceAdapter")
@RequiredArgsConstructor
public class AvisoPersistenceAdapter implements PersistencePort<Aviso, Long>, AvisoRepositoryPort {

    private final AvisoRepository repository;
    private final AvisoEntityMapper mapper;

    @Override
    public List<Aviso> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Aviso> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Aviso save(Aviso entity) {
        AvisoEntity dbEntity = mapper.toEntity(entity);
        return mapper.toDomain(repository.save(dbEntity));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Aviso> findByComunidadId(Integer comunidadId) {
        return repository.findByComunidadId(comunidadId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Aviso> findAvisosGenerales() {
        return repository.findAvisosGenerales().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Aviso> findAvisosParaUsuario(Integer comunidadId) {
        return repository.findAvisosActivosByComunidadIdOrGenerales(comunidadId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Aviso> findActivos() {
        return repository.findByActivoTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
