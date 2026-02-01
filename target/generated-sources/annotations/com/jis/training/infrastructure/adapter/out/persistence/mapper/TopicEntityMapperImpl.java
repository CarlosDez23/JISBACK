package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.Topic;
import com.jis.training.infrastructure.adapter.out.persistence.entity.TopicEntity;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-01T11:44:08+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class TopicEntityMapperImpl implements TopicEntityMapper {

    @Autowired
    private MateriaEntityMapper materiaEntityMapper;

    @Override
    public Topic toDomain(TopicEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Topic topic = new Topic();

        topic.setId( entity.getId() );
        topic.setMateria( materiaEntityMapper.toDomain( entity.getMateria() ) );
        topic.setTopicName( entity.getTopicName() );

        return topic;
    }

    @Override
    public TopicEntity toEntity(Topic domain) {
        if ( domain == null ) {
            return null;
        }

        TopicEntity topicEntity = new TopicEntity();

        topicEntity.setId( domain.getId() );
        topicEntity.setMateria( materiaEntityMapper.toEntity( domain.getMateria() ) );
        topicEntity.setTopicName( domain.getTopicName() );

        return topicEntity;
    }
}
