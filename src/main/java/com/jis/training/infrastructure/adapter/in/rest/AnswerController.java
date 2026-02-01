package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.AnswerService;
import com.jis.training.domain.model.Answer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
@Tag(name = "Answer", description = "API de Respuestas")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService service;

    @GetMapping
    @Operation(summary = "Listar todas las respuestas")
    public List<Answer> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener respuesta por ID")
    public ResponseEntity<Answer> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear respuesta")
    public Answer create(@RequestBody Answer answer) {
        return service.create(answer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar respuesta")
    public Answer update(@PathVariable Long id, @RequestBody Answer answer) {
        return service.update(id, answer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar respuesta")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/by-question/{questionId}")
    @Operation(summary = "Obtener respuestas por pregunta", description = "Devuelve todas las respuestas asociadas a una pregunta")
    public List<Answer> getByQuestionId(@PathVariable Long questionId) {
        return service.findByQuestionId(questionId);
    }
}
