package com.jis.training.domain.port.out;

import com.jis.training.domain.model.UserAnswer;

import java.util.List;

public interface UserAnswerRepositoryPort {
    List<UserAnswer> findByUsuarioId(Long usuarioId);
    List<UserAnswer> findByQuestionId(Long questionId);
}
