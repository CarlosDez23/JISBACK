package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.SimulacroPregunta;
import com.jis.training.infrastructure.adapter.out.persistence.entity.SimulacroPreguntaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { QuestionEntityMapper.class, SimulacroEntityMapper.class })
public interface SimulacroPreguntaEntityMapper {
    SimulacroPregunta toDomain(SimulacroPreguntaEntity entity);

    SimulacroPreguntaEntity toEntity(SimulacroPregunta domain);
}
