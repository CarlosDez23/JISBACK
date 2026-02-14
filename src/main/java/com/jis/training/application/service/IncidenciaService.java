package com.jis.training.application.service;

import com.jis.training.domain.model.EstadoIncidencia;
import com.jis.training.domain.model.Incidencia;
import com.jis.training.domain.model.IncidenciaComentario;
import com.jis.training.domain.port.out.IncidenciaComentarioRepositoryPort;
import com.jis.training.domain.port.out.IncidenciaRepositoryPort;
import com.jis.training.domain.port.out.PersistencePort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IncidenciaService extends GenericCrudService<Incidencia, Long> {

    private final IncidenciaRepositoryPort incidenciaRepositoryPort;
    private final IncidenciaComentarioRepositoryPort comentarioRepositoryPort;

    public IncidenciaService(
            @Qualifier("incidenciaPersistenceAdapter") PersistencePort<Incidencia, Long> persistencePort,
            IncidenciaRepositoryPort incidenciaRepositoryPort,
            IncidenciaComentarioRepositoryPort comentarioRepositoryPort) {
        super(persistencePort);
        this.incidenciaRepositoryPort = incidenciaRepositoryPort;
        this.comentarioRepositoryPort = comentarioRepositoryPort;
    }

    public List<Incidencia> findByUsuarioId(Long usuarioId) {
        return incidenciaRepositoryPort.findByUsuarioId(usuarioId);
    }

    public List<Incidencia> findByEstado(EstadoIncidencia estado) {
        return incidenciaRepositoryPort.findByEstado(estado);
    }

    public List<Incidencia> findAllOrderByFechaDesc() {
        return incidenciaRepositoryPort.findAllOrderByFechaDesc();
    }

    public long countNovedadesByUsuarioId(Long usuarioId) {
        return incidenciaRepositoryPort.countNovedadesByUsuarioId(usuarioId);
    }

    @Transactional
    public void marcarNovedadesLeidas(Long incidenciaId) {
        incidenciaRepositoryPort.marcarNovedadesLeidas(incidenciaId);
    }

    @Transactional
    public void cambiarEstado(Long incidenciaId, EstadoIncidencia estado) {
        incidenciaRepositoryPort.cambiarEstado(incidenciaId, estado);
    }

    public List<IncidenciaComentario> getComentarios(Long incidenciaId) {
        return comentarioRepositoryPort.findByIncidenciaId(incidenciaId);
    }

    @Transactional
    public IncidenciaComentario addComentario(IncidenciaComentario comentario) {
        return comentarioRepositoryPort.save(comentario);
    }

    @Transactional
    public void deleteAllByUsuarioId(Long usuarioId) {
        // Primero eliminar comentarios que el usuario hizo en incidencias de otros
        comentarioRepositoryPort.deleteByUsuarioId(usuarioId);
        // Luego eliminar las incidencias del usuario (cascade elimina sus comentarios)
        incidenciaRepositoryPort.deleteByUsuarioId(usuarioId);
    }
}
