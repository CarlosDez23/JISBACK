package com.jis.training.domain.port.out;

import com.jis.training.domain.model.IncidenciaComentario;

import java.util.List;

public interface IncidenciaComentarioRepositoryPort {
    List<IncidenciaComentario> findByIncidenciaId(Long incidenciaId);
    IncidenciaComentario save(IncidenciaComentario comentario);
    void deleteByUsuarioId(Long usuarioId);
}
