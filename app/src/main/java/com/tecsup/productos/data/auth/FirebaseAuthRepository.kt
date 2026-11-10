package com.tecsup.productos.data.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.productos.domain.model.Rol
import com.tecsup.productos.domain.model.Sesion
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> = try {
        val uid = auth.signInWithEmailAndPassword(correo, clave).await().user!!.uid
        Result.success(leerSesion(uid, correo))
    } catch (e: Exception) {
        Result.failure(Exception(mensajeDe(e), e))   // mensaje en español para la UI
    }

    override suspend fun sesionActual(): Sesion? {
        val u = auth.currentUser ?: return null      // Firebase guarda la sesión entre aperturas
        return leerSesion(u.uid, u.email ?: "")
    }

    override suspend fun registrar(nombre: String, correo: String, clave: String): Result<Unit> = try {
        val nuevo = auth.createUserWithEmailAndPassword(correo.trim(), clave).await().user
            ?: throw IllegalStateException("No se pudo crear la cuenta")
        try {
            nuevo.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(nombre.trim()).build()).await()
        } catch (_: Exception) { /* La cuenta ya fue creada; el nombre es secundario. */ }
        // Firebase autentica al registrarse: este flujo exige volver a Login.
        auth.signOut()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception(when (e) {
            is FirebaseAuthUserCollisionException -> "Este correo ya está registrado"
            is FirebaseNetworkException -> "No hay conexión a internet"
            else -> e.message ?: "No se pudo registrar la cuenta"
        }, e))
    }

    override suspend fun recuperarPassword(correo: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(correo.trim()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception(if(e is FirebaseNetworkException) "No hay conexión a internet" else e.message ?: "No se pudo enviar la solicitud", e))
    }

    override fun cerrarSesion() = auth.signOut()

    private suspend fun leerSesion(uid: String, correo: String): Sesion {
        val doc = db.collection("usuarios").document(uid).get().await()
        val rol = if (doc.getString("rol") == "admin") Rol.ADMIN else Rol.USUARIO
        return Sesion(uid, correo, rol)
    }

    private fun mensajeDe(e: Exception) = when (e) {
        is FirebaseAuthInvalidCredentialsException,
        is FirebaseAuthInvalidUserException -> "Correo o contraseña incorrectos"
        is FirebaseNetworkException -> "No hay conexión a internet"
        else -> e.message ?: "No se pudo iniciar sesión"
    }
}