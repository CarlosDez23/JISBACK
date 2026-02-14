package com.jis.training.domain.port.out;

import com.jis.training.domain.model.EstadoIncidencia;
import com.jis.training.domain.model.Incidencia;

import java.util.List;

public interface IncidenciaRepositoryPort {
    List<Incidencia> findByUsuarioId(Long usuarioId);
    List<Incidencia> findByEstado(EstadoIncidencia estado);
    List<Incidencia> findAllOrderByFechaDesc();
    long countNovedadesByUsuarioId(Long usuarioId);
    void marcarNovedadesLeidas(Long incidenciaId);
    void cambiarEstado(Long incidenciaId, EstadoIncidencia estado);
    void deleteByUsuarioId(Long usuarioId);
}
