package com.jis.training.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubmitTestRequest(
        @NotNull(message = "El ID de usuario es obligatorio")
        Long usuarioId,

        @NotEmpty(message = "La lista de respuestas no puede estar vacía")
        List<AnswerItem> respuestas
) {
    public record AnswerItem(
            @NotNull(message = "El ID de la pregunta es obligatorio")
            Long questionId,

            @NotNull(message = "El ID de la respuesta es obligatorio")
            Long answerId,

            @NotNull(message = "El campo isCorrect es obligatorio")
            Boolean isCorrect
    ) {}
}
