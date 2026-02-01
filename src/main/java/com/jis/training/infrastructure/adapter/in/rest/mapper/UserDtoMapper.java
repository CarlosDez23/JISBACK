package com.jis.training.infrastructure.adapter.in.rest.mapper;

import com.jis.training.domain.model.User;
import com.jis.training.infrastructure.adapter.in.rest.dto.UserRequest;
import com.jis.training.infrastructure.adapter.in.rest.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    @Mapping(target = "id", ignore = true)
    User toDomain(UserRequest request);

    UserResponse toResponse(User domain);
}
