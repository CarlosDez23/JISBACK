package com.jis.training.application.service;

import com.jis.training.domain.model.Answer;
import com.jis.training.domain.port.out.AnswerRepositoryPort;
import com.jis.training.domain.port.out.PersistencePort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService extends GenericCrudService<Answer, Long> {

    private final AnswerRepositoryPort answerRepositoryPort;

    public AnswerService(
            @Qualifier("answerPersistenceAdapter") PersistencePort<Answer, Long> persistencePort,
            AnswerRepositoryPort answerRepositoryPort) {
        super(persistencePort);
        this.answerRepositoryPort = answerRepositoryPort;
    }

    public List<Answer> findByQuestionId(Long questionId) {
        return answerRepositoryPort.findByQuestionId(questionId);
    }
}
