package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Question;
import com.jis.training.infrastructure.adapter.out.persistence.entity.QuestionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { TopicEntityMapper.class })
public interface QuestionEntityMapper {
    Question toDomain(QuestionEntity entity);

    QuestionEntity toEntity(Question domain);
}
