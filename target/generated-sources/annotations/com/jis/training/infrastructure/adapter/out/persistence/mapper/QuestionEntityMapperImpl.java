package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Question;
import com.jis.training.infrastructure.adapter.out.persistence.entity.QuestionEntity;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-01T11:44:08+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class QuestionEntityMapperImpl implements QuestionEntityMapper {

    @Autowired
    private TopicEntityMapper topicEntityMapper;

    @Override
    public Question toDomain(QuestionEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Question question = new Question();

        question.setId( entity.getId() );
        question.setQuestionText( entity.getQuestionText() );
        question.setTopic( topicEntityMapper.toDomain( entity.getTopic() ) );

        return question;
    }

    @Override
    public QuestionEntity toEntity(Question domain) {
        if ( domain == null ) {
            return null;
        }

        QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setId( domain.getId() );
        questionEntity.setQuestionText( domain.getQuestionText() );
        questionEntity.setTopic( topicEntityMapper.toEntity( domain.getTopic() ) );

        return questionEntity;
    }
}
