package com.tecsup.productos.ui.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productos.data.repository.ItemRepository
import com.tecsup.productos.domain.model.Item
import com.tecsup.productos.ui.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DetalleViewModel(
    private val repo: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: Int = checkNotNull(savedStateHandle["id"])
    private val _estado = MutableStateFlow<UiState<Item>>(UiState.Cargando)
    val estado: StateFlow<UiState<Item>> = _estado.asStateFlow()
    private val _sinInternet = MutableStateFlow(false)
    val sinInternet: StateFlow<Boolean> = _sinInternet.asStateFlow()
    val catalogo: StateFlow<List<Item>> = repo.items.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5_000), emptyList())
    val favorito: StateFlow<Boolean> = repo.items.map { lista ->
        lista.firstOrNull { it.id == id }?.esFavorito ?: false
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init { cargar() }

    fun alternarFavorito() = viewModelScope.launch { repo.alternarFavorito(id) }

    fun cargar() {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            _sinInternet.value = false
            repo.obtenerDetalle(id)
                .onSuccess { _estado.value = UiState.Exito(it) }
                .onFailure { error ->
                    // No se inventan datos: cuando no hay red se muestra la ficha básica persistida.
                    val local = repo.items.first().firstOrNull { it.id == id }
                    if (local != null) {
                        _sinInternet.value = true
                        _estado.value = UiState.Exito(local)
                    } else _estado.value = UiState.Error(mensajeDe(error))
                }
        }
    }

    private fun mensajeDe(e: Throwable) = when (e) {
        is IOException -> "No hay conexión a internet"
        is HttpException -> "El servidor respondió con error ${e.code()}"
        else -> "Ocurrió un error inesperado"
    }
}
