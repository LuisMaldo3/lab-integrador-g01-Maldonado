package com.tecsup.productos.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productos.data.auth.AuthRepository
import com.tecsup.productos.domain.model.Sesion
import com.tecsup.productos.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<Sesion?>>(UiState.Cargando)
    val estado: StateFlow<UiState<Sesion?>> = _estado.asStateFlow()

    /** true cuando ya se revisó si había una sesión guardada (para elegir pantalla inicial). */
    private val _listo = MutableStateFlow(false)
    val listo: StateFlow<Boolean> = _listo.asStateFlow()

    init {
        viewModelScope.launch {
            _estado.value = UiState.Exito(repo.sesionActual())   // se mantiene al reabrir la app
            _listo.value = true
        }
    }

    fun iniciarSesion(correo: String, clave: String) {
        // Validación local: NO se llama a Firebase con campos vacíos
        if (correo.isBlank() || clave.isBlank()) {
            _estado.value = UiState.Error("Completa el correo y la contraseña")
            return
        }
        if (!correo.contains("@")) {
            _estado.value = UiState.Error("El correo no tiene un formato válido")
            return
        }
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            repo.iniciarSesion(correo.trim(), clave)
                .onSuccess { _estado.value = UiState.Exito(it) }
                .onFailure { _estado.value = UiState.Error(it.message ?: "No se pudo iniciar sesión") }
        }
    }

    fun cerrarSesion() {
        repo.cerrarSesion()
        _estado.value = UiState.Exito(null)
    }

    val sesion: Sesion? get() = (_estado.value as? UiState.Exito)?.datos
}