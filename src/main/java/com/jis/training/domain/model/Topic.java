package com.jis.training.domain.model;

import lombok.Data;

@Data
public class Topic {
    private Long id;
    private String topicName;
    private Materia materia;
}
