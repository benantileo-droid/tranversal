package com.example.ms_user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Schema(description = "Entidad de usuario del sistema")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del usuario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El username no puede estar vacío")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    @Column(unique = true, nullable = false)
    @Schema(description = "Nombre de usuario único", example = "juan123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Column(nullable = false)
    @Schema(description = "Contraseña del usuario (se almacena encriptada)", example = "miPassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank(message = "El rol no puede estar vacío")
    @Pattern(regexp = "ADMIN|USER", message = "El rol debe ser ADMIN o USER")
    @Column(nullable = false)
    @Schema(description = "Rol del usuario", example = "USER", allowableValues = {"USER", "ADMIN"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String role;
}
