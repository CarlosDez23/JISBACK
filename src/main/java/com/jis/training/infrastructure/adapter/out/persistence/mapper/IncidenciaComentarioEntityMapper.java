package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.IncidenciaComentario;
import com.jis.training.infrastructure.adapter.out.persistence.entity.IncidenciaComentarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UsuarioEntityMapper.class})
public interface IncidenciaComentarioEntityMapper {

    @Mapping(source = "usuario", target = "usuario")
    @Mapping(target = "incidencia", ignore = true)
    IncidenciaComentario toDomain(IncidenciaComentarioEntity entity);

    @Mapping(source = "usuario", target = "usuario")
    @Mapping(source = "incidencia", target = "incidencia")
    IncidenciaComentarioEntity toEntity(IncidenciaComentario domain);
}
