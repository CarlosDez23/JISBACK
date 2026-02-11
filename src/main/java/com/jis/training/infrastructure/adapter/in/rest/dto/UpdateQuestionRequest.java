package com.jis.training.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UpdateQuestionRequest(
        @NotBlank(message = "questionText cannot be blank")
        String questionText,

        @NotEmpty(message = "answers cannot be empty")
        List<UpdateAnswerRequest> answers
) {
    public record UpdateAnswerRequest(
            Long id,
            String answerText,
            boolean isCorrect,
            String explanation
    ) {}
}
