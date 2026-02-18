package com.jis.training.application.service;

import com.jis.training.domain.model.Question;
import com.jis.training.domain.model.Topic;
import com.jis.training.domain.port.out.AnswerRepositoryPort;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.QuestionRepositoryPort;
import com.jis.training.domain.port.out.TopicRepositoryPort;
import com.jis.training.domain.port.out.UserAnswerRepositoryPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TopicService extends GenericCrudService<Topic, Long> {

    private final TopicRepositoryPort topicRepositoryPort;
    private final QuestionRepositoryPort questionRepositoryPort;
    private final AnswerRepositoryPort answerRepositoryPort;
    private final UserAnswerRepositoryPort userAnswerRepositoryPort;

    public TopicService(
            @Qualifier("topicPersistenceAdapter") PersistencePort<Topic, Long> persistencePort,
            TopicRepositoryPort topicRepositoryPort,
            QuestionRepositoryPort questionRepositoryPort,
            AnswerRepositoryPort answerRepositoryPort,
            UserAnswerRepositoryPort userAnswerRepositoryPort) {
        super(persistencePort);
        this.topicRepositoryPort = topicRepositoryPort;
        this.questionRepositoryPort = questionRepositoryPort;
        this.answerRepositoryPort = answerRepositoryPort;
        this.userAnswerRepositoryPort = userAnswerRepositoryPort;
    }

    public List<Topic> findByMateriaId(Long materiaId) {
        return topicRepositoryPort.findByMateriaId(materiaId);
    }

    public long countQuestionsByTopicId(Long topicId) {
        return questionRepositoryPort.countByTopicId(topicId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        List<Long> questionIds = questionRepositoryPort.findByTopicId(id)
                .stream()
                .map(Question::getId)
                .toList();

        if (!questionIds.isEmpty()) {
            userAnswerRepositoryPort.deleteByQuestionIds(questionIds);
            answerRepositoryPort.deleteByQuestionIds(questionIds);
            questionRepositoryPort.deleteByTopicId(id);
        }

        persistencePort.deleteById(id);
    }
}
