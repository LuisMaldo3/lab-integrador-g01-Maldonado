package com.tecsup.productos.data.mapper

import com.tecsup.productos.data.local.ItemEntity
import com.tecsup.productos.data.remote.dto.ItemDto
import com.tecsup.productos.domain.model.Item
import java.util.Locale

/** 549.0 -> "$ 549.00" */
fun formatearPrecio(precio: Double): String = "$ " + String.format(Locale.US, "%.2f", precio)

fun ItemDto.subtitulo(): String = listOfNotNull(brand, formatearPrecio(price)).joinToString(" · ")

// ---- Dto -> modelo (lista en memoria mientras no hay Room, y detalle) ----
fun ItemDto.aModelo(esFavorito: Boolean = false) = Item(
    id = id,
    titulo = title,
    subtitulo = subtitulo(),
    imagenUrl = thumbnail,
    precio = price,
    esFavorito = esFavorito,
    descripcion = description,
    marca = brand,
    categoria = category,
    calificacion = rating,
    stock = stock,
    descuento = discountPercentage,
    imagenes = images
)

// ---- Dto -> Entity (lo que se guarda en Room) ----
fun ItemDto.aEntity(esFavorito: Boolean = false) = ItemEntity(
    id = id,
    titulo = title,
    subtitulo = subtitulo(),
    imagenUrl = thumbnail,
    precio = price,
    esFavorito = esFavorito
)

// ---- Entity -> modelo (lo que ve la UI) ----
fun ItemEntity.aModelo() = Item(
    id = id,
    titulo = titulo,
    subtitulo = subtitulo,
    imagenUrl = imagenUrl,
    precio = precio,
    esFavorito = esFavorito
)