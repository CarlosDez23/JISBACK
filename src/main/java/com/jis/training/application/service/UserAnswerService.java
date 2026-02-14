package com.jis.training.application.service;

import com.jis.training.domain.model.Answer;
import com.jis.training.domain.model.Question;
import com.jis.training.domain.model.UserAnswer;
import com.jis.training.domain.model.Usuario;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.UserAnswerRepositoryPort;
import com.jis.training.infrastructure.adapter.in.rest.dto.SubmitTestRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
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

    @Async("taskExecutor")
    @Transactional
    public void processTestAsync(SubmitTestRequest request) {
        log.info("Procesando test con {} respuestas para usuario {}",
                request.respuestas().size(), request.usuarioId());

        List<UserAnswer> answers = request.respuestas().stream()
                .map(item -> {
                    UserAnswer ua = new UserAnswer();

                    Usuario usuario = new Usuario();
                    usuario.setId(request.usuarioId());
                    ua.setUsuario(usuario);

                    Question question = new Question();
                    question.setId(item.questionId());
                    ua.setQuestion(question);

                    Answer answer = new Answer();
                    answer.setId(item.answerId());
                    ua.setAnswer(answer);

                    ua.setCorrect(item.isCorrect());

                    return ua;
                })
                .collect(Collectors.toList());

        userAnswerRepositoryPort.saveAll(answers);

        log.info("Test procesado correctamente: {} respuestas guardadas para usuario {}",
                answers.size(), request.usuarioId());
    }
}
