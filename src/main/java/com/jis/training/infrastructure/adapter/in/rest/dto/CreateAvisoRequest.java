package com.jis.training.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAvisoRequest(
        @NotBlank(message = "El título es obligatorio")
        String tituloAviso,

        @NotBlank(message = "El cuerpo del aviso es obligatorio")
        String cuerpoAviso,

        Integer comunidadId,

        boolean activo
) {}
