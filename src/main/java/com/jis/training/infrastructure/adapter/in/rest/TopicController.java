package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.TopicService;
import com.jis.training.domain.model.Topic;
import com.jis.training.infrastructure.adapter.in.rest.dto.TopicSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@Tag(name = "Topic", description = "API de Topics (Temas)")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService service;

    @GetMapping
    @Operation(summary = "Listar todos los temas")
    public List<Topic> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tema por ID")
    public ResponseEntity<Topic> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear tema")
    public Topic create(@RequestBody Topic topic) {
        return service.create(topic);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tema")
    public Topic update(@PathVariable Long id, @RequestBody Topic topic) {
        return service.update(id, topic);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tema")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/by-materia/{materiaId}")
    @Operation(summary = "Obtener topics por materia", description = "Devuelve id, nombre y número de preguntas de los topics que pertenecen a una materia")
    public List<TopicSummaryResponse> getByMateriaId(@PathVariable Long materiaId) {
        return service.findByMateriaId(materiaId).stream()
                .map(t -> new TopicSummaryResponse(t.getId(), t.getTopicName(), service.countQuestionsByTopicId(t.getId())))
                .toList();
    }

    @GetMapping("/{id}/questions/count")
    @Operation(summary = "Obtener número de preguntas de un tema", description = "Devuelve el número total de preguntas asociadas a un topic")
    public ResponseEntity<Long> getQuestionsCount(@PathVariable Long id) {
        return service.getById(id)
                .map(topic -> ResponseEntity.ok(service.countQuestionsByTopicId(id)))
                .orElse(ResponseEntity.notFound().build());
    }
}
