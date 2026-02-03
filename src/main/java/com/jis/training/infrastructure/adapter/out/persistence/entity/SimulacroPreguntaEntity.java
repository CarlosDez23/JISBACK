package com.jis.training.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "simulacro_pregunta", schema = "jis_training")
@Data
public class SimulacroPreguntaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pregunta")
    private QuestionEntity pregunta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_simulacro")
    private SimulacroEntity simulacro;
}
