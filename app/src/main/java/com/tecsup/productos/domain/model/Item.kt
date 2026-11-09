package com.tecsup.productos.domain.model

data class Item(
    val id: Int,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val precio: Double,
    val esFavorito: Boolean = false,
    val descripcion: String? = null,
    val marca: String? = null,
    val categoria: String? = null,
    val calificacion: Double? = null,
    val stock: Int? = null,
    val descuento: Double? = null,
    val imagenes: List<String> = emptyList()
)