package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.MateriaService;
import com.jis.training.application.service.UsuarioService;
import com.jis.training.domain.model.Materia;
import com.jis.training.domain.model.Usuario;
import com.jis.training.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materias")
@Tag(name = "Materia", description = "API de Materias")
@RequiredArgsConstructor
public class MateriaController {

    private final MateriaService service;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @GetMapping("/mis-materias")
    @Operation(summary = "Obtener materias de la comunidad del usuario autenticado")
    public ResponseEntity<List<Materia>> getMisMaterias(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtService.extractUserId(authHeader.substring(7));
        Usuario usuario = usuarioService.getById(userId).orElse(null);
        if (usuario == null || usuario.getComunidad() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Materia> materias = service.findByComunidadId(usuario.getComunidad().getId());
        return ResponseEntity.ok(materias);
    }

    @GetMapping
    @Operation(summary = "Listar todas las materias")
    public List<Materia> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener materia por ID")
    public ResponseEntity<Materia> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear materia")
    public Materia create(@RequestBody Materia materia) {
        return service.create(materia);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar materia")
    public Materia update(@PathVariable Integer id, @RequestBody Materia materia) {
        return service.update(id, materia);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar materia")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
