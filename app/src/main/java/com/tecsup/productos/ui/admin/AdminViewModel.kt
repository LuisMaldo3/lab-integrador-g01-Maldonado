package com.tecsup.productos.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productos.data.repository.ItemRepository
import com.tecsup.productos.domain.model.Item
import com.tecsup.productos.notification.Notificador
import com.tecsup.productos.ui.UiState
import kotlinx.coroutines.flow.*
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
    val catalogo: StateFlow<List<Item>> = repo.items.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5_000), emptyList())
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()
    private val _trabajando = MutableStateFlow(false)
    val trabajando: StateFlow<Boolean> = _trabajando.asStateFlow()

    init { cargarTotales() }

    fun cargarTotales() {
        viewModelScope.launch {
            try {
                _estado.value = UiState.Exito(Totales(repo.cantidadGuardada(), repo.cantidadFavoritos()))
            } catch (e: Exception) {
                _estado.value = UiState.Error(e.message ?: "No se pudieron cargar los totales")
            }
        }
    }

    fun sincronizar() {
        viewModelScope.launch {
            if (_trabajando.value) return@launch
            _trabajando.value = true
            try {
                repo.refrescar()
                    .onSuccess { n ->
                        _mensaje.value = "Se guardaron $n elementos"
                        notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                    }
                    .onFailure { _mensaje.value = mensajeDe(it) }
            } finally {
                _trabajando.value = false
                cargarTotales()
            }
        }
    }

    fun limpiarCache() {
        viewModelScope.launch {
            if (_trabajando.value) return@launch
            _trabajando.value = true
            try {
                repo.limpiarCache()
                _mensaje.value = "Caché limpiada"
            } catch (e: Exception) {
                _mensaje.value = e.message ?: "No se pudo limpiar la caché"
            } finally {
                _trabajando.value = false
                cargarTotales()
            }
        }
    }

    fun limpiarMensaje() { _mensaje.value = null }

    private fun mensajeDe(e: Throwable) = when (e) {
        is IOException -> "No hay conexión a internet"
        is HttpException -> "El servidor respondió con error ${e.code()}"
        else -> "Ocurrió un error inesperado"
    }
}
