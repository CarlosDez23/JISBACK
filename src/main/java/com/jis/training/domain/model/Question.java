package com.jis.training.domain.model;

import lombok.Data;

@Data
public class Question {
    private Long id;
    private String questionText;
    private Topic topic;
}
