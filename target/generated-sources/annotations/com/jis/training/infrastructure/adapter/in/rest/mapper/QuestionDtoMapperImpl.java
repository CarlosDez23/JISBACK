package com.jis.training.infrastructure.adapter.in.rest.mapper;

import com.jis.training.domain.model.Answer;
import com.jis.training.domain.model.QuestionWithAnswers;
import com.jis.training.infrastructure.adapter.in.rest.dto.AnswerResponse;
import com.jis.training.infrastructure.adapter.in.rest.dto.QuestionWithAnswersResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-01T11:44:08+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class QuestionDtoMapperImpl implements QuestionDtoMapper {

    @Override
    public AnswerResponse toAnswerResponse(Answer answer) {
        if ( answer == null ) {
            return null;
        }

        boolean isCorrect = false;
        Long id = null;
        String answerText = null;
        String explanation = null;

        isCorrect = answer.isCorrect();
        id = answer.getId();
        answerText = answer.getAnswerText();
        explanation = answer.getExplanation();

        AnswerResponse answerResponse = new AnswerResponse( id, answerText, isCorrect, explanation );

        return answerResponse;
    }

    @Override
    public List<AnswerResponse> toAnswerResponseList(List<Answer> answers) {
        if ( answers == null ) {
            return null;
        }

        List<AnswerResponse> list = new ArrayList<AnswerResponse>( answers.size() );
        for ( Answer answer : answers ) {
            list.add( toAnswerResponse( answer ) );
        }

        return list;
    }

    @Override
    public QuestionWithAnswersResponse toQuestionWithAnswersResponse(QuestionWithAnswers question) {
        if ( question == null ) {
            return null;
        }

        Long id = null;
        String questionText = null;
        Long topicId = null;
        String topicName = null;
        List<AnswerResponse> answers = null;

        id = question.getId();
        questionText = question.getQuestionText();
        topicId = question.getTopicId();
        topicName = question.getTopicName();
        answers = toAnswerResponseList( question.getAnswers() );

        QuestionWithAnswersResponse questionWithAnswersResponse = new QuestionWithAnswersResponse( id, questionText, topicId, topicName, answers );

        return questionWithAnswersResponse;
    }

    @Override
    public List<QuestionWithAnswersResponse> toQuestionWithAnswersResponseList(List<QuestionWithAnswers> questions) {
        if ( questions == null ) {
            return null;
        }

        List<QuestionWithAnswersResponse> list = new ArrayList<QuestionWithAnswersResponse>( questions.size() );
        for ( QuestionWithAnswers questionWithAnswers : questions ) {
            list.add( toQuestionWithAnswersResponse( questionWithAnswers ) );
        }

        return list;
    }
}
