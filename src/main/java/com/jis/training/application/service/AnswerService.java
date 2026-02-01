package com.jis.training.application.service;

import com.jis.training.domain.model.Answer;
import com.jis.training.domain.port.out.PersistencePort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AnswerService extends GenericCrudService<Answer, Long> {
    public AnswerService(@Qualifier("answerPersistenceAdapter") PersistencePort<Answer, Long> persistencePort) {
        super(persistencePort);
    }
}
