package com.example.labx.domain.model

/**
 * Usuario: Modelo de dominio para usuarios del sistema
 * 
 * En una app real:
 * - Las contraseñas se hashean (bcrypt, argon2)
 * - Se usa JWT o OAuth2 para autenticación
 * - Las credenciales se almacenan en backend seguro
 * 
 * Para fines educativos: Usamos SharedPreferences local
 * 
 * Autor: Prof. Sting Adams Parra Silva
 */
data class Usuario(
    val username: String,
    val password: String,
    val rol: Rol = Rol.USUARIO
)

enum class Rol {
    USUARIO,
    ADMIN
}
