package com.jis.training.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "simulacro", schema = "jis_training")
@Data
public class SimulacroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nonmbre_simulacro", nullable = false)
    private String nombreSimulacro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comunidad")
    private ComunidadEntity comunidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia")
    private MateriaEntity materia;
}
