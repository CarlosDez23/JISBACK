package com.jis.training.domain.model;

import lombok.Data;

@Data
public class Simulacro {
    private Long id;
    private String nombreSimulacro;
    private Comunidad comunidad;
    private Materia materia;
    private Integer tiempoLimiteSegundos;
    private TipoPenalizacion tipoPenalizacion;
}
