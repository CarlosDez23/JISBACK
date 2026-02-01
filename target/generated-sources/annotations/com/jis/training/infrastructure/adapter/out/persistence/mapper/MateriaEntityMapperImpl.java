package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Materia;
import com.jis.training.infrastructure.adapter.out.persistence.entity.MateriaEntity;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-01T11:44:07+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class MateriaEntityMapperImpl implements MateriaEntityMapper {

    @Autowired
    private ComunidadEntityMapper comunidadEntityMapper;

    @Override
    public Materia toDomain(MateriaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Materia materia = new Materia();

        materia.setComunidad( comunidadEntityMapper.toDomain( entity.getComunidad() ) );
        materia.setId( entity.getId() );
        materia.setNombreMateria( entity.getNombreMateria() );
        materia.setSigla( entity.getSigla() );

        return materia;
    }

    @Override
    public MateriaEntity toEntity(Materia domain) {
        if ( domain == null ) {
            return null;
        }

        MateriaEntity materiaEntity = new MateriaEntity();

        materiaEntity.setComunidad( comunidadEntityMapper.toEntity( domain.getComunidad() ) );
        materiaEntity.setId( domain.getId() );
        materiaEntity.setNombreMateria( domain.getNombreMateria() );
        materiaEntity.setSigla( domain.getSigla() );

        return materiaEntity;
    }
}
