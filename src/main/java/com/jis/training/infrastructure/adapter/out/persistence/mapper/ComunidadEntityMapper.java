package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Comunidad;
import com.jis.training.infrastructure.adapter.out.persistence.entity.ComunidadEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ComunidadEntityMapper {
    Comunidad toDomain(ComunidadEntity entity);

    ComunidadEntity toEntity(Comunidad domain);
}
