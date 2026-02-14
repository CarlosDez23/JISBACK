package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.IncidenciaService;
import com.jis.training.application.service.UsuarioService;
import com.jis.training.domain.model.*;
import com.jis.training.infrastructure.adapter.in.rest.dto.*;
import com.jis.training.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/incidencias")
@Tag(name = "Incidencias", description = "API de Gestión de Incidencias")
@RequiredArgsConstructor
public class IncidenciaController {

    private final IncidenciaService incidenciaService;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    // ==================== ENDPOINTS PARA USUARIOS ====================

    @GetMapping("/mis-incidencias")
    @Operation(summary = "Obtener mis incidencias",
               description = "Devuelve todas las incidencias del usuario autenticado")
    public ResponseEntity<List<IncidenciaResponse>> getMisIncidencias(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserId(authHeader);
        List<Incidencia> incidencias = incidenciaService.findByUsuarioId(userId);

        return ResponseEntity.ok(incidencias.stream().map(this::toResponse).toList());
    }

    @GetMapping("/mis-novedades")
    @Operation(summary = "Contar incidencias con novedades",
               description = "Devuelve el número de incidencias con actualizaciones pendientes")
    public ResponseEntity<Map<String, Long>> getMisNovedades(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserId(authHeader);
        long count = incidenciaService.countNovedadesByUsuarioId(userId);

        return ResponseEntity.ok(Map.of("novedades", count));
    }

    @PostMapping
    @Operation(summary = "Crear incidencia",
               description = "Crea una nueva incidencia")
    public ResponseEntity<IncidenciaResponse> crear(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateIncidenciaRequest request) {

        Long userId = extractUserId(authHeader);
        Optional<Usuario> usuarioOpt = usuarioService.getById(userId);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Usuario usuario = usuarioOpt.get();

        Incidencia incidencia = new Incidencia();
        incidencia.setTitulo(request.titulo());
        incidencia.setDescripcion(request.descripcion());
        incidencia.setImagenUrl(request.imagenUrl());
        incidencia.setUsuario(usuario);
        incidencia.setEstado(EstadoIncidencia.CREADA);

        Incidencia saved = incidenciaService.create(incidencia);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener incidencia por ID")
    public ResponseEntity<IncidenciaResponse> getById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        Long userId = extractUserId(authHeader);

        Optional<Incidencia> incidenciaOpt = incidenciaService.getById(id);
        if (incidenciaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Incidencia incidencia = incidenciaOpt.get();

        Optional<Usuario> usuarioOpt = usuarioService.getById(userId);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Usuario usuario = usuarioOpt.get();
        boolean esDueno = incidencia.getUsuario().getId().equals(userId);
        boolean esAdmin = usuario.isAdmin();

        if (!esDueno && !esAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Si es el dueño, marcar novedades como leídas
        if (esDueno && incidencia.isTieneNovedades()) {
            incidenciaService.marcarNovedadesLeidas(id);
            incidencia.setTieneNovedades(false);
        }

        return ResponseEntity.ok(toResponse(incidencia));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar incidencia",
               description = "El usuario solo puede eliminar sus propias incidencias")
    public ResponseEntity<Void> eliminar(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        Long userId = extractUserId(authHeader);

        Optional<Incidencia> incidenciaOpt = incidenciaService.getById(id);
        if (incidenciaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Incidencia incidencia = incidenciaOpt.get();

        Optional<Usuario> usuarioOpt = usuarioService.getById(userId);
        boolean esDueno = incidencia.getUsuario().getId().equals(userId);
        boolean esAdmin = usuarioOpt.isPresent() && usuarioOpt.get().isAdmin();

        if (!esDueno && !esAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        incidenciaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/comentarios")
    @Operation(summary = "Añadir comentario a incidencia")
    public ResponseEntity<IncidenciaResponse.ComentarioResponse> addComentario(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody AddComentarioRequest request) {

        Long userId = extractUserId(authHeader);

        Optional<Incidencia> incidenciaOpt = incidenciaService.getById(id);
        if (incidenciaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Incidencia incidencia = incidenciaOpt.get();

        Optional<Usuario> usuarioOpt = usuarioService.getById(userId);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Usuario usuario = usuarioOpt.get();
        boolean esDueno = incidencia.getUsuario().getId().equals(userId);
        boolean esAdmin = usuario.isAdmin();

        if (!esDueno && !esAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        IncidenciaComentario comentario = new IncidenciaComentario();
        comentario.setIncidencia(incidencia);
        comentario.setUsuario(usuario);
        comentario.setTexto(request.texto());

        IncidenciaComentario saved = incidenciaService.addComentario(comentario);

        return ResponseEntity.status(HttpStatus.CREATED).body(toComentarioResponse(saved, usuario));
    }

    // ==================== ENDPOINTS PARA ADMINISTRADORES ====================

    @GetMapping("/admin/todas")
    @Operation(summary = "[Admin] Listar todas las incidencias")
    public ResponseEntity<List<IncidenciaResponse>> getAllAdmin(
            @RequestHeader("Authorization") String authHeader) {

        if (!isAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Incidencia> incidencias = incidenciaService.findAllOrderByFechaDesc();
        return ResponseEntity.ok(incidencias.stream().map(this::toResponse).toList());
    }

    @GetMapping("/admin/estado/{estado}")
    @Operation(summary = "[Admin] Listar incidencias por estado")
    public ResponseEntity<List<IncidenciaResponse>> getByEstadoAdmin(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable EstadoIncidencia estado) {

        if (!isAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Incidencia> incidencias = incidenciaService.findByEstado(estado);
        return ResponseEntity.ok(incidencias.stream().map(this::toResponse).toList());
    }

    @PatchMapping("/admin/{id}/estado")
    @Operation(summary = "[Admin] Cambiar estado de incidencia")
    public ResponseEntity<Void> cambiarEstado(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request) {

        if (!isAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (incidenciaService.getById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        incidenciaService.cambiarEstado(id, request.estado());
        return ResponseEntity.ok().build();
    }

    // ==================== HELPERS ====================

    private Long extractUserId(String authHeader) {
        String token = authHeader.substring(7);
        return jwtService.extractUserId(token);
    }

    private boolean isAdmin(String authHeader) {
        Long userId = extractUserId(authHeader);
        return usuarioService.getById(userId)
                .map(Usuario::isAdmin)
                .orElse(false);
    }

    private IncidenciaResponse toResponse(Incidencia incidencia) {
        List<IncidenciaResponse.ComentarioResponse> comentarios = Collections.emptyList();

        if (incidencia.getComentarios() != null) {
            comentarios = incidencia.getComentarios().stream()
                    .map(c -> {
                        Usuario u = c.getUsuario();
                        return new IncidenciaResponse.ComentarioResponse(
                                c.getId(),
                                u != null ? u.getId() : null,
                                u != null ? u.getNombre() + " " + u.getApellidos() : "Usuario",
                                u != null && u.isAdmin(),
                                c.getTexto(),
                                c.getFechaCreacion()
                        );
                    })
                    .toList();
        }

        Usuario usuario = incidencia.getUsuario();

        return new IncidenciaResponse(
                incidencia.getId(),
                incidencia.getTitulo(),
                incidencia.getDescripcion(),
                incidencia.getImagenUrl(),
                usuario != null ? usuario.getId() : null,
                usuario != null ? usuario.getNombre() + " " + usuario.getApellidos() : "Usuario",
                usuario != null ? usuario.getCorreoElectronico() : null,
                incidencia.getFechaCreacion(),
                incidencia.getEstado(),
                incidencia.isTieneNovedades(),
                comentarios
        );
    }

    private IncidenciaResponse.ComentarioResponse toComentarioResponse(IncidenciaComentario comentario, Usuario usuario) {
        return new IncidenciaResponse.ComentarioResponse(
                comentario.getId(),
                usuario.getId(),
                usuario.getNombre() + " " + usuario.getApellidos(),
                usuario.isAdmin(),
                comentario.getTexto(),
                comentario.getFechaCreacion()
        );
    }
}
