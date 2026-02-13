package com.jis.training.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios", schema = "jis_training")
@Data
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(name = "correo_electronico", nullable = false, unique = true)
    private String correoElectronico;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    @Column(name = "password_change_required", nullable = false)
    private boolean passwordChangeRequired = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comunidad", nullable = false)
    private ComunidadEntity comunidad;
}
