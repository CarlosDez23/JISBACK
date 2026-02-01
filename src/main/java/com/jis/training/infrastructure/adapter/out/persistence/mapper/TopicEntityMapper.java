package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Topic;
import com.jis.training.infrastructure.adapter.out.persistence.entity.TopicEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { MateriaEntityMapper.class })
public interface TopicEntityMapper {
    Topic toDomain(TopicEntity entity);

    TopicEntity toEntity(Topic domain);
}
