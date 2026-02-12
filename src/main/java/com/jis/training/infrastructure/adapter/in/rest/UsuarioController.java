package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.UsuarioService;
import com.jis.training.domain.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuario", description = "API de Usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public List<Usuario> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<Usuario> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{correoElectronico}")
    @Operation(summary = "Obtener usuario por correo electrónico")
    public ResponseEntity<Usuario> getByCorreoElectronico(@PathVariable String correoElectronico) {
        return service.findByCorreoElectronico(correoElectronico)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear usuario")
    public Usuario create(@RequestBody Usuario usuario) {
        return service.create(usuario);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public Usuario update(@PathVariable Long id, @RequestBody Usuario usuario) {
        return service.update(id, usuario);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
