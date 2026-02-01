package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.UserService;
import com.jis.training.domain.model.User;
import com.jis.training.infrastructure.adapter.in.rest.dto.UserRequest;
import com.jis.training.infrastructure.adapter.in.rest.dto.UserResponse;
import com.jis.training.infrastructure.adapter.in.rest.mapper.UserDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "API de Usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserDtoMapper mapper;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public List<UserResponse> getAll() {
        return service.getAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UserResponse> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear usuario")
    public UserResponse create(@RequestBody UserRequest request) {
        User user = mapper.toDomain(request);
        return mapper.toResponse(service.create(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public UserResponse update(@PathVariable Integer id, @RequestBody UserRequest request) {
        User user = mapper.toDomain(request);
        user.setId(id);
        return mapper.toResponse(service.update(id, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
