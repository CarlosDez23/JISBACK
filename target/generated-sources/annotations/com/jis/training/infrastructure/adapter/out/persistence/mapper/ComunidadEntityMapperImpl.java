package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Comunidad;
import com.jis.training.infrastructure.adapter.out.persistence.entity.ComunidadEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-01T11:44:07+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class ComunidadEntityMapperImpl implements ComunidadEntityMapper {

    @Override
    public Comunidad toDomain(ComunidadEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Comunidad comunidad = new Comunidad();

        comunidad.setGrupoOposicion( entity.getGrupoOposicion() );
        comunidad.setId( entity.getId() );
        comunidad.setNombre( entity.getNombre() );

        return comunidad;
    }

    @Override
    public ComunidadEntity toEntity(Comunidad domain) {
        if ( domain == null ) {
            return null;
        }

        ComunidadEntity comunidadEntity = new ComunidadEntity();

        comunidadEntity.setGrupoOposicion( domain.getGrupoOposicion() );
        comunidadEntity.setId( domain.getId() );
        comunidadEntity.setNombre( domain.getNombre() );

        return comunidadEntity;
    }
}
