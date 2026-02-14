package com.jis.training.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record AddComentarioRequest(
        @NotBlank(message = "El texto del comentario es obligatorio")
        String texto
) {}
