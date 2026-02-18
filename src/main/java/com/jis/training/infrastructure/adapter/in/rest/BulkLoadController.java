package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.BulkLoadService;
import com.jis.training.application.service.UsuarioService;
import com.jis.training.domain.model.Usuario;
import com.jis.training.infrastructure.adapter.in.rest.dto.BulkLoadResult;
import com.jis.training.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/bulk")
@Tag(name = "Carga Masiva", description = "API de carga masiva de datos via Excel")
@RequiredArgsConstructor
public class BulkLoadController {

    private final BulkLoadService bulkLoadService;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    // ======================== USUARIOS ========================

    @GetMapping("/usuarios/plantilla")
    @Operation(summary = "Descargar plantilla Excel de usuarios",
            description = "Genera y descarga la plantilla Excel para carga masiva de usuarios con desplegables de comunidades")
    public ResponseEntity<byte[]> downloadUsuariosTemplate() {
        try {
            byte[] template = bulkLoadService.generateUsuariosTemplate();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=plantilla_usuarios.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(template);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/usuarios", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cargar usuarios desde Excel",
            description = "Procesa un archivo Excel con la plantilla de usuarios rellena y da de alta los usuarios")
    public ResponseEntity<BulkLoadResult> uploadUsuarios(@RequestParam("file") MultipartFile file) {
        try {
            validateExcelFile(file);
            BulkLoadResult result = bulkLoadService.processUsuariosExcel(file);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ======================== TEMAS ========================

    @GetMapping("/temas/plantilla")
    @Operation(summary = "Descargar plantilla Excel de temas",
            description = "Genera y descarga la plantilla Excel para carga de un tema con preguntas y respuestas. Las materias del desplegable son las de la comunidad del usuario autenticado.")
    public ResponseEntity<byte[]> downloadTemasTemplate(
            @RequestHeader("Authorization") String authHeader) {
        try {
            Usuario usuario = extractUsuario(authHeader);
            if (usuario == null || usuario.getComunidad() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            byte[] template = bulkLoadService.generateTemasTemplate(usuario.getComunidad().getId());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=plantilla_tema.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(template);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/temas", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cargar tema con preguntas desde Excel",
            description = "Procesa un archivo Excel con la plantilla de tema rellena y da de alta el tema con sus preguntas y respuestas")
    public ResponseEntity<BulkLoadResult> uploadTemas(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {
        try {
            validateExcelFile(file);
            Usuario usuario = extractUsuario(authHeader);
            if (usuario == null || usuario.getComunidad() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            BulkLoadResult result = bulkLoadService.processTemasExcel(file, usuario.getComunidad().getId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ======================== UTILIDADES ========================

    private Usuario extractUsuario(String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);
        return usuarioService.getById(userId).orElse(null);
    }

    private void validateExcelFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx")) {
            throw new IllegalArgumentException("El archivo debe ser un Excel (.xlsx)");
        }
    }
}
