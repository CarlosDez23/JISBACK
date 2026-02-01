package com.jis.training.domain.port.in;

import com.jis.training.domain.model.QuestionWithAnswers;

import java.util.List;

public interface GenerateQuizUseCase {
    List<QuestionWithAnswers> generateQuiz(List<Long> topicIds, int numberOfQuestions);
}
