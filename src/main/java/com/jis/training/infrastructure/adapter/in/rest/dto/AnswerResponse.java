package com.jis.training.infrastructure.adapter.in.rest.dto;

public record AnswerResponse(
        Long id,
        String answerText,
        boolean isCorrect,
        String explanation
) {}
