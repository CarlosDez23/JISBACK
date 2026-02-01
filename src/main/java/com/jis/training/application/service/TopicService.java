package com.jis.training.application.service;

import com.jis.training.domain.model.Topic;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.QuestionRepositoryPort;
import com.jis.training.domain.port.out.TopicRepositoryPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService extends GenericCrudService<Topic, Long> {

    private final TopicRepositoryPort topicRepositoryPort;
    private final QuestionRepositoryPort questionRepositoryPort;

    public TopicService(
            @Qualifier("topicPersistenceAdapter") PersistencePort<Topic, Long> persistencePort,
            TopicRepositoryPort topicRepositoryPort,
            QuestionRepositoryPort questionRepositoryPort) {
        super(persistencePort);
        this.topicRepositoryPort = topicRepositoryPort;
        this.questionRepositoryPort = questionRepositoryPort;
    }

    public List<Topic> findByMateriaId(Long materiaId) {
        return topicRepositoryPort.findByMateriaId(materiaId);
    }

    public long countQuestionsByTopicId(Long topicId) {
        return questionRepositoryPort.countByTopicId(topicId);
    }
}
