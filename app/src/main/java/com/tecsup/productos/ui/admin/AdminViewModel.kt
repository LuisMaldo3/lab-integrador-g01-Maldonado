package com.tecsup.productos.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productos.data.repository.ItemRepository
import com.tecsup.productos.notification.Notificador
import com.tecsup.productos.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class Totales(val guardados: Int, val favoritos: Int)

class AdminViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<Totales>>(UiState.Cargando)
    val estado: StateFlow<UiState<Totales>> = _estado.asStateFlow()

    /** Mensaje corto tras una acción (ej. "Se guardaron 70 elementos"). */
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _trabajando = MutableStateFlow(false)
    val trabajando: StateFlow<Boolean> = _trabajando.asStateFlow()

    init { cargarTotales() }

    fun cargarTotales() {
        viewModelScope.launch {
            _estado.value = UiState.Exito(Totales(repo.cantidadGuardada(), repo.cantidadFavoritos()))
        }
    }

    fun sincronizar() {
        viewModelScope.launch {
            _trabajando.value = true
            repo.refrescar()
                .onSuccess { n ->
                    _mensaje.value = "Se guardaron $n elementos"
                    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                }
                .onFailure { _mensaje.value = mensajeDe(it) }
            _trabajando.value = false
            cargarTotales()
        }
    }

    fun limpiarCache() {
        viewModelScope.launch {
            repo.limpiarCache()
            _mensaje.value = "Caché limpiada"
            cargarTotales()
        }
    }

    fun limpiarMensaje() { _mensaje.value = null }

    private fun mensajeDe(e: Throwable) = when (e) {
        is IOException -> "No hay conexión a internet"
        is HttpException -> "El servidor respondió con error ${e.code()}"
        else -> "Ocurrió un error inesperado"
    }
}