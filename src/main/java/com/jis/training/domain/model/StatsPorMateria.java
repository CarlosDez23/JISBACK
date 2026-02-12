package com.jis.training.domain.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatsPorMateria {
    private Long usuarioId;
    private Long materiaId;
    private String nombreMateria;
    private Long totalRespuestas;
    private Long aciertos;
    private BigDecimal porcentajeAcierto;
}
