package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.StatsPorTopic;
import com.jis.training.infrastructure.adapter.out.persistence.entity.StatsPorTopicEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatsPorTopicEntityMapper {
    StatsPorTopic toDomain(StatsPorTopicEntity entity);
}
