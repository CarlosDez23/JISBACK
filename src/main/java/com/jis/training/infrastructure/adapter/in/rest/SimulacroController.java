package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.SimulacroService;
import com.jis.training.domain.model.Simulacro;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/simulacros")
@Tag(name = "Simulacro", description = "API de Simulacros")
@RequiredArgsConstructor
public class SimulacroController {

    private final SimulacroService service;

    @GetMapping
    @Operation(summary = "Listar todos los simulacros")
    public List<Simulacro> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener simulacro por ID")
    public ResponseEntity<Simulacro> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear simulacro")
    public Simulacro create(@RequestBody Simulacro simulacro) {
        return service.create(simulacro);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar simulacro")
    public Simulacro update(@PathVariable Long id, @RequestBody Simulacro simulacro) {
        return service.update(id, simulacro);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar simulacro")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
