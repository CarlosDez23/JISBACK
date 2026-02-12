package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.StatsService;
import com.jis.training.domain.model.StatsPorMateria;
import com.jis.training.domain.model.StatsPorTopic;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Stats", description = "API de Estadísticas de Usuario")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService service;

    @GetMapping("/topic/usuario/{usuarioId}")
    @Operation(summary = "Obtener estadísticas por topic de un usuario")
    public List<StatsPorTopic> getStatsPorTopicByUsuarioId(@PathVariable Long usuarioId) {
        return service.getStatsPorTopicByUsuarioId(usuarioId);
    }

    @GetMapping("/materia/usuario/{usuarioId}")
    @Operation(summary = "Obtener estadísticas por materia de un usuario")
    public List<StatsPorMateria> getStatsPorMateriaByUsuarioId(@PathVariable Long usuarioId) {
        return service.getStatsPorMateriaByUsuarioId(usuarioId);
    }
}
