package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Aviso;
import com.jis.training.infrastructure.adapter.out.persistence.entity.AvisoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ComunidadEntityMapper.class})
public interface AvisoEntityMapper {
    @Mapping(source = "comunidad", target = "comunidad")
    Aviso toDomain(AvisoEntity entity);

    @Mapping(source = "comunidad", target = "comunidad")
    AvisoEntity toEntity(Aviso domain);
}
