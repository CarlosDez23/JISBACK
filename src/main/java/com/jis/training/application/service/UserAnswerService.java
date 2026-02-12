package com.jis.training.application.service;

import com.jis.training.domain.model.UserAnswer;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.UserAnswerRepositoryPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAnswerService extends GenericCrudService<UserAnswer, Long> {

    private final UserAnswerRepositoryPort userAnswerRepositoryPort;

    public UserAnswerService(
            @Qualifier("userAnswerPersistenceAdapter") PersistencePort<UserAnswer, Long> persistencePort,
            UserAnswerRepositoryPort userAnswerRepositoryPort) {
        super(persistencePort);
        this.userAnswerRepositoryPort = userAnswerRepositoryPort;
    }

    public List<UserAnswer> findByUsuarioId(Long usuarioId) {
        return userAnswerRepositoryPort.findByUsuarioId(usuarioId);
    }

    public List<UserAnswer> findByQuestionId(Long questionId) {
        return userAnswerRepositoryPort.findByQuestionId(questionId);
    }
}
