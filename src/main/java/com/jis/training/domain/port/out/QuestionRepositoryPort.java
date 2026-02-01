package com.jis.training.domain.port.out;

import com.jis.training.domain.model.Answer;
import com.jis.training.domain.model.Question;

import java.util.List;

public interface QuestionRepositoryPort {
    List<Question> findByTopicIds(List<Long> topicIds);
    List<Answer> findAnswersByQuestionIds(List<Long> questionIds);
    long countByTopicId(Long topicId);
}
