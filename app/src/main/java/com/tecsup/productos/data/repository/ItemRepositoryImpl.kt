package com.tecsup.productos.data.repository

import com.tecsup.productos.data.mapper.aModelo
import com.tecsup.productos.data.remote.ApiService
import com.tecsup.productos.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class ItemRepositoryImpl(
    private val api: ApiService,
    private val categoria: String
) : ItemRepository {

    private val memoria = MutableStateFlow<List<Item>>(emptyList())

    override val items: Flow<List<Item>> = memoria.asStateFlow()
    override val favoritos: Flow<List<Item>> = memoria.map { lista -> lista.filter { it.esFavorito } }

    override suspend fun refrescar(): Result<Int> = try {
        val favoritosActuales = memoria.value.filter { it.esFavorito }.map { it.id }.toSet()
        val nuevos = api.listar(categoria).products
            .map { it.aModelo(esFavorito = it.id in favoritosActuales) }
            .sortedBy { it.titulo }
        memoria.value = nuevos
        Result.success(nuevos.size)
    } catch (e: IOException) {          // sin internet
        Result.failure(e)
    } catch (e: HttpException) {        // 404, 500...
        Result.failure(e)
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> = try {
        val esFav = memoria.value.any { it.id == id && it.esFavorito }
        Result.success(api.detalle(id).aModelo(esFavorito = esFav))
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(e)
    }

    override suspend fun alternarFavorito(id: Int) {
        memoria.value = memoria.value.map { if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it }
    }

    override suspend fun cantidadGuardada(): Int = memoria.value.size
    override suspend fun cantidadFavoritos(): Int = memoria.value.count { it.esFavorito }
    override suspend fun limpiarCache() { memoria.value = emptyList() }
}