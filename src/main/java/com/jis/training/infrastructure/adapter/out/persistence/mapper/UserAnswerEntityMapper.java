package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.UserAnswer;
import com.jis.training.infrastructure.adapter.out.persistence.entity.UserAnswerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UsuarioEntityMapper.class, QuestionEntityMapper.class, AnswerEntityMapper.class})
public interface UserAnswerEntityMapper {
    @Mapping(source = "correct", target = "correct")
    UserAnswer toDomain(UserAnswerEntity entity);

    @Mapping(source = "correct", target = "correct")
    UserAnswerEntity toEntity(UserAnswer domain);
}
