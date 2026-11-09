package com.tecsup.productos.data.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
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