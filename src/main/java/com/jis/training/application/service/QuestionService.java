package com.jis.training.application.service;

import com.jis.training.domain.model.Answer;
import com.jis.training.domain.model.Question;
import com.jis.training.domain.model.QuestionWithAnswers;
import com.jis.training.domain.port.in.GenerateQuizUseCase;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.domain.port.out.QuestionRepositoryPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService extends GenericCrudService<Question, Long> implements GenerateQuizUseCase {

    private final QuestionRepositoryPort questionRepositoryPort;
    private final AnswerService answerService;

    public QuestionService(
            @Qualifier("questionPersistenceAdapter") PersistencePort<Question, Long> persistencePort,
            QuestionRepositoryPort questionRepositoryPort,
            AnswerService answerService) {
        super(persistencePort);
        this.questionRepositoryPort = questionRepositoryPort;
        this.answerService = answerService;
    }

    public List<Question> findByTopicId(Long topicId) {
        return questionRepositoryPort.findByTopicId(topicId);
    }

    @Transactional
    public Question createWithAnswers(Question question, List<Answer> answers) {
        Question savedQuestion = create(question);

        for (Answer answer : answers) {
            answer.setQuestion(savedQuestion);
            answerService.create(answer);
        }

        return savedQuestion;
    }

    @Override
    public List<QuestionWithAnswers> generateQuiz(List<Long> topicIds, int numberOfQuestions) {
        List<Question> allQuestions = questionRepositoryPort.findByTopicIds(topicIds);

        Map<Long, List<Question>> questionsByTopic = allQuestions.stream()
                .collect(Collectors.groupingBy(q -> q.getTopic().getId()));

        List<Question> selectedQuestions = distributeQuestions(questionsByTopic, topicIds, numberOfQuestions);

        List<Long> questionIds = selectedQuestions.stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        List<Answer> allAnswers = questionRepositoryPort.findAnswersByQuestionIds(questionIds);

        Map<Long, List<Answer>> answersByQuestionId = allAnswers.stream()
                .collect(Collectors.groupingBy(a -> a.getQuestion().getId()));

        List<QuestionWithAnswers> result = selectedQuestions.stream()
                .map(q -> new QuestionWithAnswers(
                        q.getId(),
                        q.getQuestionText(),
                        q.getTopic().getId(),
                        q.getTopic().getTopicName(),
                        answersByQuestionId.getOrDefault(q.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());

        Collections.shuffle(result);
        return result;
    }

    private List<Question> distributeQuestions(
            Map<Long, List<Question>> questionsByTopic,
            List<Long> topicIds,
            int totalQuestions) {

        List<Question> selected = new ArrayList<>();
        int topicsCount = topicIds.size();

        if (topicsCount == 0) {
            return selected;
        }

        int basePerTopic = totalQuestions / topicsCount;
        int remainder = totalQuestions % topicsCount;

        Random random = new Random();
        int topicIndex = 0;

        for (Long topicId : topicIds) {
            List<Question> topicQuestions = questionsByTopic.getOrDefault(topicId, Collections.emptyList());

            int toSelect = basePerTopic + (topicIndex < remainder ? 1 : 0);
            toSelect = Math.min(toSelect, topicQuestions.size());

            List<Question> shuffled = new ArrayList<>(topicQuestions);
            Collections.shuffle(shuffled, random);

            selected.addAll(shuffled.subList(0, toSelect));
            topicIndex++;
        }

        return selected;
    }
}
