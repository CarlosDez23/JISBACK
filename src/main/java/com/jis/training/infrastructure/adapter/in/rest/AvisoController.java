package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.AvisoService;
import com.jis.training.application.service.ComunidadService;
import com.jis.training.application.service.UsuarioService;
import com.jis.training.domain.model.Aviso;
import com.jis.training.domain.model.Comunidad;
import com.jis.training.domain.model.Usuario;
import com.jis.training.infrastructure.adapter.in.rest.dto.AvisoResponse;
import com.jis.training.infrastructure.adapter.in.rest.dto.CreateAvisoRequest;
import com.jis.training.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avisos")
@Tag(name = "Avisos", description = "API de Avisos/Notificaciones")
@RequiredArgsConstructor
public class AvisoController {

    private final AvisoService avisoService;
    private final ComunidadService comunidadService;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @GetMapping
    @Operation(summary = "Listar todos los avisos")
    public List<AvisoResponse> getAll() {
        return avisoService.getAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener aviso por ID")
    public ResponseEntity<AvisoResponse> getById(@PathVariable Long id) {
        return avisoService.getById(id)
                .map(aviso -> ResponseEntity.ok(toResponse(aviso)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mis-avisos")
    @Operation(summary = "Obtener avisos para el usuario autenticado",
               description = "Devuelve los avisos generales y los específicos de la comunidad del usuario")
    public ResponseEntity<List<AvisoResponse>> getMisAvisos(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        Usuario usuario = usuarioService.getById(userId).orElse(null);
        if (usuario == null || usuario.getComunidad() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<AvisoResponse> avisos = avisoService
                .findAvisosParaUsuario(usuario.getComunidad().getId())
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(avisos);
    }

    @GetMapping("/comunidad/{comunidadId}")
    @Operation(summary = "Obtener avisos por comunidad")
    public List<AvisoResponse> getByComunidadId(@PathVariable Integer comunidadId) {
        return avisoService.findByComunidadId(comunidadId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/generales")
    @Operation(summary = "Obtener avisos generales")
    public List<AvisoResponse> getGenerales() {
        return avisoService.findAvisosGenerales().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener avisos activos")
    public List<AvisoResponse> getActivos() {
        return avisoService.findActivos().stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @Operation(summary = "Crear aviso")
    public ResponseEntity<AvisoResponse> create(@Valid @RequestBody CreateAvisoRequest request) {
        Comunidad comunidad = null;
        if (request.comunidadId() != null) {
            comunidad = comunidadService.getById(request.comunidadId()).orElse(null);
            if (comunidad == null) {
                return ResponseEntity.badRequest().build();
            }
        }

        Aviso aviso = new Aviso();
        aviso.setTituloAviso(request.tituloAviso());
        aviso.setCuerpoAviso(request.cuerpoAviso());
        aviso.setComunidad(comunidad);
        aviso.setActivo(request.activo());

        Aviso saved = avisoService.create(aviso);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar aviso")
    public ResponseEntity<AvisoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateAvisoRequest request) {

        return avisoService.getById(id)
                .map(existingAviso -> {
                    Comunidad comunidad = null;
                    if (request.comunidadId() != null) {
                        comunidad = comunidadService.getById(request.comunidadId()).orElse(null);
                    }

                    existingAviso.setTituloAviso(request.tituloAviso());
                    existingAviso.setCuerpoAviso(request.cuerpoAviso());
                    existingAviso.setComunidad(comunidad);
                    existingAviso.setActivo(request.activo());

                    Aviso updated = avisoService.update(id, existingAviso);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar aviso")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (avisoService.getById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        avisoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-activo")
    @Operation(summary = "Activar/Desactivar aviso")
    public ResponseEntity<AvisoResponse> toggleActivo(@PathVariable Long id) {
        return avisoService.getById(id)
                .map(aviso -> {
                    aviso.setActivo(!aviso.isActivo());
                    Aviso updated = avisoService.update(id, aviso);
                    return ResponseEntity.ok(toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private AvisoResponse toResponse(Aviso aviso) {
        return new AvisoResponse(
                aviso.getId(),
                aviso.getTituloAviso(),
                aviso.getCuerpoAviso(),
                aviso.getComunidad() != null ? aviso.getComunidad().getId() : null,
                aviso.getComunidad() != null ? aviso.getComunidad().getNombre() : null,
                aviso.getFechaCreacion(),
                aviso.isActivo(),
                aviso.getComunidad() == null
        );
    }
}
