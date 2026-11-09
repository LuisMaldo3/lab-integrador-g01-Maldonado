package com.tecsup.productos.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ---------- Endpoint 1: GET /products/category/{slug} ----------
// dummyjson envuelve la lista: { "products": [ {...}, {...} ], "total": 16, "skip": 0, "limit": 16 }

@Serializable
data class ProductosResponseDto(
    val products: List<ItemDto> = emptyList(),
    val total: Int = 0,
    val skip: Int = 0,
    val limit: Int = 0
)

// ---------- Endpoint 2: GET /products/{id} ----------
// Devuelve un producto con la MISMA forma que cada elemento de la lista,
// por eso un solo DTO sirve para los dos endpoints.
@Serializable
data class ItemDto(
    val id: Int,
    val title: String,
    val description: String = "",
    val price: Double = 0.0,
    @SerialName("discountPercentage") val discountPercentage: Double = 0.0,
    val rating: Double = 0.0,
    val stock: Int = 0,
    val brand: String? = null,          // algunos productos no traen marca
    val category: String = "",
    val thumbnail: String? = null,
    val images: List<String> = emptyList()
)