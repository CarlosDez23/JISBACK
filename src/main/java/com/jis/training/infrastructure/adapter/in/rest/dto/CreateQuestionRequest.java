package com.jis.training.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateQuestionRequest(
        @NotBlank(message = "questionText cannot be blank")
        String questionText,

        @NotNull(message = "topic cannot be null")
        TopicIdRequest topic,

        @NotEmpty(message = "answers cannot be empty")
        List<AnswerRequest> answers
) {
    public record TopicIdRequest(Long id) {}
}
