package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.SimulacroService;
import com.jis.training.domain.model.Comunidad;
import com.jis.training.domain.model.Materia;
import com.jis.training.domain.model.Simulacro;
import com.jis.training.infrastructure.adapter.in.rest.dto.CreateSimulacroRequest;
import com.jis.training.infrastructure.adapter.in.rest.dto.GenerateSimulacroRequest;
import com.jis.training.infrastructure.adapter.in.rest.dto.SimulacroResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    @Operation(summary = "Listar todos los simulacros con número de preguntas")
    public List<SimulacroResponse> getAll() {
        return service.getAllWithPreguntaCount();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener simulacro por ID")
    public ResponseEntity<Simulacro> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear simulacro con preguntas")
    public Simulacro create(@Valid @RequestBody CreateSimulacroRequest request) {
        Simulacro simulacro = new Simulacro();
        simulacro.setNombreSimulacro(request.nombreSimulacro());

        if (request.comunidadId() != null) {
            Comunidad comunidad = new Comunidad();
            comunidad.setId(request.comunidadId().intValue());
            simulacro.setComunidad(comunidad);
        }

        if (request.materiaId() != null) {
            Materia materia = new Materia();
            materia.setId(request.materiaId().intValue());
            simulacro.setMateria(materia);
        }

        return service.createWithPreguntas(simulacro, request.preguntaIds());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar simulacro")
    public Simulacro update(@PathVariable Long id, @RequestBody Simulacro simulacro) {
        return service.update(id, simulacro);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar simulacro", description = "Elimina el simulacro y todas sus preguntas asociadas")
    public void delete(@PathVariable Long id) {
        service.deleteWithPreguntas(id);
    }

    @PostMapping("/generate")
    @Operation(summary = "Generar simulacro con preguntas aleatorias",
               description = "Crea un simulacro seleccionando aleatoriamente preguntas de cada tema según la cantidad especificada")
    public Simulacro generate(@Valid @RequestBody GenerateSimulacroRequest request) {
        Simulacro simulacro = new Simulacro();
        simulacro.setNombreSimulacro(request.nombreSimulacro());

        if (request.comunidadId() != null) {
            Comunidad comunidad = new Comunidad();
            comunidad.setId(request.comunidadId().intValue());
            simulacro.setComunidad(comunidad);
        }

        if (request.materiaId() != null) {
            Materia materia = new Materia();
            materia.setId(request.materiaId().intValue());
            simulacro.setMateria(materia);
        }

        return service.generateSimulacro(simulacro, request.preguntasPorTema());
    }
}
