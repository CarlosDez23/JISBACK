package com.jis.training.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAnswer {
    private Long id;
    private Usuario usuario;
    private Question question;
    private Answer answer;
    private boolean isCorrect;
    private LocalDateTime answeredAt;
}
