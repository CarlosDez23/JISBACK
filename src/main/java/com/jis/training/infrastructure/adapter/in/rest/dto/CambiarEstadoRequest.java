package com.jis.training.infrastructure.adapter.in.rest.dto;

import com.jis.training.domain.model.EstadoIncidencia;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoIncidencia estado
) {}
