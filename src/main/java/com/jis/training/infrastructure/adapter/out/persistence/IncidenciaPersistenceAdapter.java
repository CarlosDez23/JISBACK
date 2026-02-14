package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.EstadoIncidencia;
import com.jis.training.domain.model.Incidencia;
import com.jis.training.domain.port.out.IncidenciaRepositoryPort;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.infrastructure.adapter.out.persistence.entity.IncidenciaEntity;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.IncidenciaEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.IncidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("incidenciaPersistenceAdapter")
@RequiredArgsConstructor
public class IncidenciaPersistenceAdapter implements PersistencePort<Incidencia, Long>, IncidenciaRepositoryPort {

    private final IncidenciaRepository repository;
    private final IncidenciaEntityMapper mapper;

    @Override
    public List<Incidencia> findAll() {
        return repository.findAllByOrderByFechaCreacionDesc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Incidencia> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Incidencia save(Incidencia entity) {
        IncidenciaEntity dbEntity = mapper.toEntity(entity);
        return mapper.toDomain(repository.save(dbEntity));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Incidencia> findByUsuarioId(Long usuarioId) {
        return repository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Incidencia> findByEstado(EstadoIncidencia estado) {
        return repository.findByEstadoOrderByFechaCreacionDesc(estado).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Incidencia> findAllOrderByFechaDesc() {
        return repository.findAllByOrderByFechaCreacionDesc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countNovedadesByUsuarioId(Long usuarioId) {
        return repository.countNovedadesByUsuarioId(usuarioId);
    }

    @Override
    @Transactional
    public void marcarNovedadesLeidas(Long incidenciaId) {
        repository.updateTieneNovedades(incidenciaId, false);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long incidenciaId, EstadoIncidencia estado) {
        repository.updateEstado(incidenciaId, estado);
    }

    @Override
    @Transactional
    public void deleteByUsuarioId(Long usuarioId) {
        repository.deleteByUsuarioId(usuarioId);
    }
}
