package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.ComunidadService;
import com.jis.training.domain.model.Comunidad;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunidades")
@Tag(name = "Comunidad", description = "API de Comunidades")
@RequiredArgsConstructor
public class ComunidadController {

    private final ComunidadService service;

    @GetMapping
    @Operation(summary = "Listar todas las comunidades")
    public List<Comunidad> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener comunidad por ID")
    public ResponseEntity<Comunidad> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear comunidad")
    public Comunidad create(@RequestBody Comunidad comunidad) {
        return service.create(comunidad);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar comunidad")
    public Comunidad update(@PathVariable Integer id, @RequestBody Comunidad comunidad) {
        return service.update(id, comunidad);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar comunidad")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
