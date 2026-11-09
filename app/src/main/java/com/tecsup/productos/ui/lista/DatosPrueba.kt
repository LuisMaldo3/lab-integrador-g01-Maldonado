package com.tecsup.productos.ui.lista

import com.tecsup.productos.domain.model.Item
object DatosPrueba {
    private fun foto(id: Int) = "https://picsum.photos/seed/producto$id/200"

    val items = listOf(
        Item(1, "iPhone 9", "Apple · $ 549.00", foto(1), 549.0),
        Item(2, "iPhone X", "Apple · $ 899.00", foto(2), 899.0, esFavorito = true),
        Item(3, "Samsung Universe 9", "Samsung · $ 1249.00", foto(3), 1249.0),
        Item(4, "OPPOF19", "OPPO · $ 280.00", foto(4), 280.0),
        Item(5, "Huawei P30", "Huawei · $ 499.00", foto(5), 499.0)
    )
}