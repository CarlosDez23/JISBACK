package com.jis.training.domain.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatsPorTopic {
    private Long usuarioId;
    private Long topicId;
    private String topicName;
    private Long totalRespuestas;
    private Long aciertos;
    private BigDecimal porcentajeAcierto;
}
