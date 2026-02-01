package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Answer;
import com.jis.training.infrastructure.adapter.out.persistence.entity.AnswerEntity;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-01T11:44:08+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class AnswerEntityMapperImpl implements AnswerEntityMapper {

    @Autowired
    private QuestionEntityMapper questionEntityMapper;

    @Override
    public Answer toDomain(AnswerEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Answer answer = new Answer();

        answer.setAnswerText( entity.getAnswerText() );
        answer.setCorrect( entity.isCorrect() );
        answer.setExplanation( entity.getExplanation() );
        answer.setId( entity.getId() );
        answer.setQuestion( questionEntityMapper.toDomain( entity.getQuestion() ) );

        return answer;
    }

    @Override
    public AnswerEntity toEntity(Answer domain) {
        if ( domain == null ) {
            return null;
        }

        AnswerEntity answerEntity = new AnswerEntity();

        answerEntity.setAnswerText( domain.getAnswerText() );
        answerEntity.setCorrect( domain.isCorrect() );
        answerEntity.setExplanation( domain.getExplanation() );
        answerEntity.setId( domain.getId() );
        answerEntity.setQuestion( questionEntityMapper.toEntity( domain.getQuestion() ) );

        return answerEntity;
    }
}
