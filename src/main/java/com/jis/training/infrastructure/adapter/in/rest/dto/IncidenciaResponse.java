package com.jis.training.infrastructure.adapter.in.rest.dto;

import com.jis.training.domain.model.EstadoIncidencia;

import java.time.LocalDateTime;
import java.util.List;

public record IncidenciaResponse(
        Long id,
        String titulo,
        String descripcion,
        String imagenUrl,
        Long usuarioId,
        String usuarioNombre,
        String usuarioEmail,
        LocalDateTime fechaCreacion,
        EstadoIncidencia estado,
        boolean tieneNovedades,
        List<ComentarioResponse> comentarios
) {
    public record ComentarioResponse(
            Long id,
            Long usuarioId,
            String usuarioNombre,
            boolean esAdmin,
            String texto,
            LocalDateTime fechaCreacion
    ) {}
}
