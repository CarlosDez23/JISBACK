package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.ComunidadService;
import com.jis.training.application.service.UsuarioService;
import com.jis.training.domain.model.Comunidad;
import com.jis.training.domain.model.Usuario;
import com.jis.training.infrastructure.adapter.in.rest.dto.UpdateUsuarioRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    private final ComunidadService comunidadService;

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
    public ResponseEntity<Usuario> update(@PathVariable Long id, @Valid @RequestBody UpdateUsuarioRequest request) {
        Comunidad comunidad = comunidadService.getById(request.comunidadId()).orElse(null);
        if (comunidad == null) {
            return ResponseEntity.badRequest().build();
        }

        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(request.nombre());
        usuario.setApellidos(request.apellidos());
        usuario.setCorreoElectronico(request.correoElectronico());
        usuario.setAdmin(request.isAdmin());
        usuario.setComunidad(comunidad);

        return ResponseEntity.ok(service.update(id, usuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/comunidad/{comunidadId}")
    @Operation(summary = "Obtener usuarios por comunidad")
    public List<Usuario> getByComunidadId(@PathVariable Integer comunidadId) {
        return service.findByComunidadId(comunidadId);
    }
}
