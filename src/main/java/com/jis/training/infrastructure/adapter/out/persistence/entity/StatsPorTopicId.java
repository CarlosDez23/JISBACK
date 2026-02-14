package com.jis.training.infrastructure.adapter.out.persistence.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class StatsPorTopicId implements Serializable {
    private Long usuarioId;
    private Long topicId;
}
