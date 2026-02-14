package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.UserAnswerService;
import com.jis.training.domain.model.UserAnswer;
import com.jis.training.infrastructure.adapter.in.rest.dto.SubmitTestRequest;
import com.jis.training.infrastructure.adapter.in.rest.dto.SubmitTestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-answers")
@Tag(name = "UserAnswer", description = "API de Respuestas de Usuario")
@RequiredArgsConstructor
public class UserAnswerController {

    private final UserAnswerService service;

    @GetMapping
    @Operation(summary = "Listar todas las respuestas de usuarios")
    public List<UserAnswer> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener respuesta por ID")
    public ResponseEntity<UserAnswer> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener respuestas por usuario")
    public List<UserAnswer> getByUsuarioId(@PathVariable Long usuarioId) {
        return service.findByUsuarioId(usuarioId);
    }

    @GetMapping("/question/{questionId}")
    @Operation(summary = "Obtener respuestas por pregunta")
    public List<UserAnswer> getByQuestionId(@PathVariable Long questionId) {
        return service.findByQuestionId(questionId);
    }

    @PostMapping
    @Operation(summary = "Registrar respuesta de usuario individual")
    public UserAnswer create(@RequestBody UserAnswer userAnswer) {
        return service.create(userAnswer);
    }

    @PostMapping("/submit-test")
    @Operation(summary = "Enviar resultados de test completo",
               description = "Guarda todas las respuestas de un test de forma asíncrona. " +
                             "Devuelve 202 Accepted inmediatamente. Las respuestas se procesan en segundo plano.")
    public ResponseEntity<SubmitTestResponse> submitTest(@Valid @RequestBody SubmitTestRequest request) {
        service.processTestAsync(request);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(SubmitTestResponse.accepted(request.respuestas().size()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar respuesta")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
