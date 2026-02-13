package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.domain.model.Comunidad;
import com.jis.training.domain.model.Usuario;
import com.jis.training.application.service.UsuarioService;
import com.jis.training.application.service.ComunidadService;
import com.jis.training.infrastructure.adapter.in.rest.dto.ChangePasswordRequest;
import com.jis.training.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.jis.training.infrastructure.adapter.in.rest.dto.LoginResponse;
import com.jis.training.infrastructure.adapter.in.rest.dto.RegisterRequest;
import com.jis.training.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "API de Autenticación")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final ComunidadService comunidadService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.correoElectronico(),
                            request.password()
                    )
            );

            Usuario usuario = usuarioService.findByCorreoElectronico(request.correoElectronico())
                    .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

            User userDetails = new User(
                    usuario.getCorreoElectronico(),
                    usuario.getUserPassword(),
                    Collections.emptyList()
            );

            Map<String, Object> extraClaims = Map.of(
                    "userId", usuario.getId(),
                    "pcr", usuario.isPasswordChangeRequired()
            );
            String token = jwtService.generateToken(extraClaims, userDetails);

            return ResponseEntity.ok(new LoginResponse(
                    token,
                    usuario.getId(),
                    usuario.getCorreoElectronico(),
                    usuario.getNombre(),
                    usuario.getApellidos(),
                    usuario.isAdmin(),
                    usuario.isPasswordChangeRequired()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario y devuelve un token JWT")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioService.existsByCorreoElectronico(request.correoElectronico())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El correo electrónico ya está registrado");
        }

        Comunidad comunidad = comunidadService.getById(request.comunidadId())
                .orElse(null);

        if (comunidad == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Comunidad no encontrada");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setApellidos(request.apellidos());
        usuario.setCorreoElectronico(request.correoElectronico());
        usuario.setUserPassword(passwordEncoder.encode(request.password()));
        usuario.setAdmin(request.isAdmin());
        usuario.setPasswordChangeRequired(true);
        usuario.setComunidad(comunidad);

        Usuario savedUsuario = usuarioService.create(usuario);

        User userDetails = new User(
                savedUsuario.getCorreoElectronico(),
                savedUsuario.getUserPassword(),
                Collections.emptyList()
        );

        Map<String, Object> extraClaims = Map.of(
                "userId", savedUsuario.getId(),
                "pcr", true
        );
        String token = jwtService.generateToken(extraClaims, userDetails);

        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponse(
                token,
                savedUsuario.getId(),
                savedUsuario.getCorreoElectronico(),
                savedUsuario.getNombre(),
                savedUsuario.getApellidos(),
                savedUsuario.isAdmin(),
                true
        ));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Cambiar contraseña", description = "Cambia la contraseña del usuario autenticado")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordRequest request) {

        String token = authHeader.substring(7);
        String correoElectronico = jwtService.extractUsername(token);

        Usuario usuario = usuarioService.findByCorreoElectronico(correoElectronico)
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }

        usuario.setUserPassword(passwordEncoder.encode(request.newPassword()));
        usuario.setPasswordChangeRequired(false);
        usuarioService.updatePassword(usuario.getId(), usuario.getUserPassword());

        User userDetails = new User(
                usuario.getCorreoElectronico(),
                usuario.getUserPassword(),
                Collections.emptyList()
        );

        Map<String, Object> extraClaims = Map.of(
                "userId", usuario.getId(),
                "pcr", false
        );
        String newToken = jwtService.generateToken(extraClaims, userDetails);

        return ResponseEntity.ok(new LoginResponse(
                newToken,
                usuario.getId(),
                usuario.getCorreoElectronico(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.isAdmin(),
                false
        ));
    }
}
