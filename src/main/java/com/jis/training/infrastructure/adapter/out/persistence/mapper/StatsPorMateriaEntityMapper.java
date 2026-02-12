package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.StatsPorMateria;
import com.jis.training.infrastructure.adapter.out.persistence.entity.StatsPorMateriaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatsPorMateriaEntityMapper {
    StatsPorMateria toDomain(StatsPorMateriaEntity entity);
}
