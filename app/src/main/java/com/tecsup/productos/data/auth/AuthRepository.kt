package com.tecsup.productos.data.auth

import com.tecsup.productos.domain.model.Sesion

interface AuthRepository {
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>
    suspend fun sesionActual(): Sesion?      // null si no hay sesión
    /** Compatibilidad con implementaciones de prueba existentes. */
    suspend fun registrar(nombre: String, correo: String, clave: String): Result<Unit> =
        Result.failure(UnsupportedOperationException("Registro no disponible"))
    suspend fun recuperarPassword(correo: String): Result<Unit> =
        Result.failure(UnsupportedOperationException("Recuperación no disponible"))
    fun cerrarSesion()
}