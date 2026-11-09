package com.tecsup.productos.fakes

import com.tecsup.productos.data.repository.ItemRepository
import com.tecsup.productos.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Repositorio falso para pruebas: simula Room con un StateFlow en memoria
 * y simula la API con la lista `remotos` o con un error `falla`.
 */
class FakeItemRepository(
    private val remotos: List<Item> = emptyList(),
    guardadosAlInicio: List<Item> = emptyList(),
    var falla: Throwable? = null
) : ItemRepository {

    private val tabla = MutableStateFlow(guardadosAlInicio)
    var vecesRefrescado = 0
        private set

    override val items: Flow<List<Item>> = tabla
    override val favoritos: Flow<List<Item>> = tabla.map { l -> l.filter { it.esFavorito } }

    override suspend fun refrescar(): Result<Int> {
        vecesRefrescado++
        falla?.let { return Result.failure(it) }
        val favs = tabla.value.filter { it.esFavorito }.map { it.id }.toSet()
        tabla.value = remotos.map { it.copy(esFavorito = it.id in favs) }
        return Result.success(remotos.size)
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> =
        tabla.value.firstOrNull { it.id == id }?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("No existe el producto $id"))

    override suspend fun alternarFavorito(id: Int) {
        tabla.value = tabla.value.map { if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it }
    }

    override suspend fun cantidadGuardada() = tabla.value.size
    override suspend fun cantidadFavoritos() = tabla.value.count { it.esFavorito }
    override suspend fun limpiarCache() { tabla.value = emptyList() }
}