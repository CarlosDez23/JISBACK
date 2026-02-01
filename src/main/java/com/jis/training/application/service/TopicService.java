package com.jis.training.application.service;

import com.jis.training.domain.model.Topic;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.TopicRepositoryPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService extends GenericCrudService<Topic, Long> {

    private final TopicRepositoryPort topicRepositoryPort;

    public TopicService(
            @Qualifier("topicPersistenceAdapter") PersistencePort<Topic, Long> persistencePort,
            TopicRepositoryPort topicRepositoryPort) {
        super(persistencePort);
        this.topicRepositoryPort = topicRepositoryPort;
    }

    public List<Topic> findByMateriaId(Long materiaId) {
        return topicRepositoryPort.findByMateriaId(materiaId);
    }
}
