package com.jis.training.infrastructure.adapter.in.rest.dto;

import com.jis.training.domain.model.TipoPenalizacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public record GenerateSimulacroRequest(
        @NotBlank(message = "nombreSimulacro cannot be blank")
        String nombreSimulacro,

        Long comunidadId,

        Long materiaId,

        Integer tiempoLimiteSegundos,

        TipoPenalizacion tipoPenalizacion,

        @NotEmpty(message = "preguntasPorTema cannot be empty")
        Map<Long, Integer> preguntasPorTema
) {}
