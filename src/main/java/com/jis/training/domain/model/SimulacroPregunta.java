package com.jis.training.domain.model;

import lombok.Data;

@Data
public class SimulacroPregunta {
    private Long id;
    private Question pregunta;
    private Simulacro simulacro;
}
