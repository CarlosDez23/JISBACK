package com.jis.training.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Entity
@Immutable
@Table(name = "stats_por_topic", schema = "jis_training")
@Data
public class StatsPorTopicEntity {
    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "topic_id")
    private Long topicId;

    @Column(name = "topic_name")
    private String topicName;

    @Column(name = "total_respuestas")
    private Long totalRespuestas;

    @Column(name = "aciertos")
    private Long aciertos;

    @Column(name = "porcentaje_acierto")
    private BigDecimal porcentajeAcierto;
}
