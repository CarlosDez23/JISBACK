package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.SimulacroPreguntaService;
import com.jis.training.domain.model.SimulacroPregunta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/simulacro-preguntas")
@Tag(name = "SimulacroPregunta", description = "API de Preguntas de Simulacro")
@RequiredArgsConstructor
public class SimulacroPreguntaController {

    private final SimulacroPreguntaService service;

    @GetMapping
    @Operation(summary = "Listar todas las preguntas de simulacros")
    public List<SimulacroPregunta> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pregunta de simulacro por ID")
    public ResponseEntity<SimulacroPregunta> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear pregunta de simulacro")
    public SimulacroPregunta create(@RequestBody SimulacroPregunta simulacroPregunta) {
        return service.create(simulacroPregunta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pregunta de simulacro")
    public SimulacroPregunta update(@PathVariable Long id, @RequestBody SimulacroPregunta simulacroPregunta) {
        return service.update(id, simulacroPregunta);
    }

    @DeleteMapping("/simulacro/{simulacroId}/pregunta/{preguntaId}")
    @Operation(summary = "Eliminar pregunta de un simulacro", description = "Elimina una pregunta específica de un simulacro")
    public void delete(@PathVariable Long simulacroId, @PathVariable Long preguntaId) {
        service.deleteBySimulacroIdAndPreguntaId(simulacroId, preguntaId);
    }

    @GetMapping("/by-simulacro/{simulacroId}")
    @Operation(summary = "Obtener preguntas por simulacro", description = "Devuelve todas las preguntas que conforman un simulacro")
    public List<SimulacroPregunta> getBySimulacroId(@PathVariable Long simulacroId) {
        return service.findBySimulacroId(simulacroId);
    }
}
