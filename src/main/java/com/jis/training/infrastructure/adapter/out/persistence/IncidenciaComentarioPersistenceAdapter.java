package com.jis.training.infrastructure.adapter.out.persistence;

import com.jis.training.domain.model.IncidenciaComentario;
import com.jis.training.domain.port.out.IncidenciaComentarioRepositoryPort;
import com.jis.training.infrastructure.adapter.out.persistence.entity.IncidenciaComentarioEntity;
import com.jis.training.infrastructure.adapter.out.persistence.entity.IncidenciaEntity;
import com.jis.training.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import com.jis.training.infrastructure.adapter.out.persistence.mapper.IncidenciaComentarioEntityMapper;
import com.jis.training.infrastructure.adapter.out.persistence.repository.IncidenciaComentarioRepository;
import com.jis.training.infrastructure.adapter.out.persistence.repository.IncidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IncidenciaComentarioPersistenceAdapter implements IncidenciaComentarioRepositoryPort {

    private final IncidenciaComentarioRepository repository;
    private final IncidenciaRepository incidenciaRepository;
    private final IncidenciaComentarioEntityMapper mapper;

    @Override
    public List<IncidenciaComentario> findByIncidenciaId(Long incidenciaId) {
        return repository.findByIncidenciaIdOrderByFechaCreacionAsc(incidenciaId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IncidenciaComentario save(IncidenciaComentario comentario) {
        IncidenciaComentarioEntity entity = new IncidenciaComentarioEntity();
        entity.setTexto(comentario.getTexto());

        IncidenciaEntity incidencia = new IncidenciaEntity();
        incidencia.setId(comentario.getIncidencia().getId());
        entity.setIncidencia(incidencia);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(comentario.getUsuario().getId());
        entity.setUsuario(usuario);

        IncidenciaComentarioEntity saved = repository.save(entity);

        // Marcar la incidencia como que tiene novedades
        incidenciaRepository.updateTieneNovedades(comentario.getIncidencia().getId(), true);

        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteByUsuarioId(Long usuarioId) {
        repository.deleteByUsuarioId(usuarioId);
    }
}
