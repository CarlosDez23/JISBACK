package com.jis.training.domain.port.out;

import com.jis.training.domain.model.Answer;

import java.util.List;

public interface AnswerRepositoryPort {
    List<Answer> findByQuestionId(Long questionId);
}
