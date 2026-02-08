package com.jis.training.infrastructure.adapter.in.rest.dto;

import java.util.List;

public record GenerateQuizResponse(
        int totalQuestions,
        int topicsCount,
        List<QuestionWithAnswersResponse> questions,
        String pdfBase64
) {}
