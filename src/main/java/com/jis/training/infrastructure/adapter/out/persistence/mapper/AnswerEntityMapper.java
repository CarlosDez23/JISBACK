package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Answer;
import com.jis.training.infrastructure.adapter.out.persistence.entity.AnswerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { QuestionEntityMapper.class })
public interface AnswerEntityMapper {
    Answer toDomain(AnswerEntity entity);

    AnswerEntity toEntity(Answer domain);
}
