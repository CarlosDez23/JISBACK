package com.jis.training.infrastructure.adapter.out.persistence.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class StatsPorMateriaId implements Serializable {
    private Long usuarioId;
    private Long materiaId;
}
