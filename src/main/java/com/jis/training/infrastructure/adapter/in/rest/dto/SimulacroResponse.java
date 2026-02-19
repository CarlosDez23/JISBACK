package com.jis.training.infrastructure.adapter.in.rest.dto;

import com.jis.training.domain.model.Comunidad;
import com.jis.training.domain.model.Materia;
import com.jis.training.domain.model.TipoPenalizacion;

public record SimulacroResponse(
        Long id,
        String nombreSimulacro,
        Comunidad comunidad,
        Materia materia,
        Integer tiempoLimiteSegundos,
        TipoPenalizacion tipoPenalizacion,
        long totalPreguntas
) {}
