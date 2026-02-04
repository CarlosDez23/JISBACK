package com.jis.training.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateSimulacroRequest(
        @NotBlank(message = "nombreSimulacro cannot be blank")
        String nombreSimulacro,

        Long comunidadId,

        Long materiaId,

        @NotEmpty(message = "preguntaIds cannot be empty")
        List<Long> preguntaIds
) {}
