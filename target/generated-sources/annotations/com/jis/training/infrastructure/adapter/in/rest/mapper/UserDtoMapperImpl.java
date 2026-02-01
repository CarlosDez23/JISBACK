package com.jis.training.infrastructure.adapter.in.rest.mapper;

import com.jis.training.domain.model.User;
import com.jis.training.infrastructure.adapter.in.rest.dto.UserRequest;
import com.jis.training.infrastructure.adapter.in.rest.dto.UserResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-01T11:44:07+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260101-2150, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class UserDtoMapperImpl implements UserDtoMapper {

    @Override
    public User toDomain(UserRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setPassword( request.password() );
        user.setUsername( request.username() );

        return user;
    }

    @Override
    public UserResponse toResponse(User domain) {
        if ( domain == null ) {
            return null;
        }

        Integer id = null;
        String username = null;

        id = domain.getId();
        username = domain.getUsername();

        UserResponse userResponse = new UserResponse( id, username );

        return userResponse;
    }
}
