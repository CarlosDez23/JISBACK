package com.jis.training.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Entity
@Immutable
@Table(name = "stats_por_materia", schema = "jis_training")
@IdClass(StatsPorMateriaId.class)
@Data
public class StatsPorMateriaEntity {
    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Id
    @Column(name = "materia_id")
    private Long materiaId;

    @Column(name = "nombre_materia")
    private String nombreMateria;

    @Column(name = "total_respuestas")
    private Long totalRespuestas;

    @Column(name = "aciertos")
    private Long aciertos;

    @Column(name = "porcentaje_acierto")
    private BigDecimal porcentajeAcierto;
}
