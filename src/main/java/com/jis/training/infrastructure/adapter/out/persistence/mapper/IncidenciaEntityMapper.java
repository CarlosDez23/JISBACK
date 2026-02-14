package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Incidencia;
import com.jis.training.infrastructure.adapter.out.persistence.entity.IncidenciaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UsuarioEntityMapper.class, IncidenciaComentarioEntityMapper.class})
public interface IncidenciaEntityMapper {

    @Mapping(source = "usuario", target = "usuario")
    @Mapping(source = "comentarios", target = "comentarios")
    Incidencia toDomain(IncidenciaEntity entity);

    @Mapping(source = "usuario", target = "usuario")
    @Mapping(target = "comentarios", ignore = true)
    IncidenciaEntity toEntity(Incidencia domain);
}
