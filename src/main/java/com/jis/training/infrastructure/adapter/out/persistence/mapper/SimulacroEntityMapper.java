package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Simulacro;
import com.jis.training.infrastructure.adapter.out.persistence.entity.SimulacroEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { ComunidadEntityMapper.class, MateriaEntityMapper.class })
public interface SimulacroEntityMapper {
    Simulacro toDomain(SimulacroEntity entity);

    SimulacroEntity toEntity(Simulacro domain);
}
