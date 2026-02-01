package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Materia;
import com.jis.training.infrastructure.adapter.out.persistence.entity.MateriaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { ComunidadEntityMapper.class })
public interface MateriaEntityMapper {
    Materia toDomain(MateriaEntity entity);

    MateriaEntity toEntity(Materia domain);
}
