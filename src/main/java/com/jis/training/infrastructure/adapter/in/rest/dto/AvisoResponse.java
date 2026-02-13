package com.jis.training.infrastructure.adapter.in.rest.dto;

import java.time.LocalDateTime;

public record AvisoResponse(
        Long id,
        String tituloAviso,
        String cuerpoAviso,
        Integer comunidadId,
        String comunidadNombre,
        LocalDateTime fechaCreacion,
        boolean activo,
        boolean esGeneral
) {}
