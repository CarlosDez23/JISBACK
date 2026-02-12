package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Usuario;
import com.jis.training.infrastructure.adapter.out.persistence.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ComunidadEntityMapper.class})
public interface UsuarioEntityMapper {
    @Mapping(source = "comunidad", target = "comunidad")
    Usuario toDomain(UsuarioEntity entity);

    @Mapping(source = "comunidad", target = "comunidad")
    UsuarioEntity toEntity(Usuario domain);
}
