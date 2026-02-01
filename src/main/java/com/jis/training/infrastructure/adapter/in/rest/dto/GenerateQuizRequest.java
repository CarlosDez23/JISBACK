package com.jis.training.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record GenerateQuizRequest(
        @NotEmpty(message = "topicIds cannot be empty")
        List<Long> topicIds,

        @Min(value = 10, message = "numberOfQuestions must be at least 10")
        @Max(value = 100, message = "numberOfQuestions must be at most 100")
        int numberOfQuestions
) {}
