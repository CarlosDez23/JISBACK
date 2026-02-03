package com.jis.training.infrastructure.adapter.in.rest.dto;

public record AnswerRequest(
        String answerText,
        boolean isCorrect,
        String explanation
) {}
