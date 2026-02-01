package com.jis.training.domain.model;

import lombok.Data;

@Data
public class Answer {
    private Long id;
    private Question question;
    private String answerText;
    private boolean isCorrect;
    private String explanation;
}
