package com.jis.training.infrastructure.adapter.out.persistence.mapper;

import com.jis.training.domain.model.User;
import com.jis.training.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    User toDomain(UserEntity entity);

    UserEntity toEntity(User domain);
}
