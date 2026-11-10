package com.tecsup.productos.ui.comparador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productos.data.repository.ItemRepository
import com.tecsup.productos.domain.model.Item
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** La comparación usa únicamente productos locales de Room. */
class CompararViewModel(repo: ItemRepository) : ViewModel() {
    val productos: StateFlow<List<Item>> = repo.items.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )
    private val _seleccion = MutableStateFlow<List<Int>>(emptyList())
    val seleccion: StateFlow<List<Int>> = _seleccion

    fun agregar(id: Int) {
        if (id !in _seleccion.value && _seleccion.value.size < 3) {
            _seleccion.value = _seleccion.value + id
        }
    }
    fun quitar(id: Int) { _seleccion.value = _seleccion.value - id }
    fun alternar(id: Int) {
        if (id in _seleccion.value) quitar(id) else agregar(id)
    }
    fun vaciar() { _seleccion.value = emptyList() }
}
