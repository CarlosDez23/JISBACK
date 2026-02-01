package com.jis.training.infrastructure.adapter.in.rest.dto;

import java.util.List;

public record QuestionWithAnswersResponse(
        Long id,
        String questionText,
        Long topicId,
        String topicName,
        List<AnswerResponse> answers
) {}
