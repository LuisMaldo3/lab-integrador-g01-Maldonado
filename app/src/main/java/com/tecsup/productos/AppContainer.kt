package com.tecsup.productos

import android.content.Context
import androidx.room.Room
import com.tecsup.productos.data.local.AppDatabase
import com.tecsup.productos.data.remote.RetrofitClient
import com.tecsup.productos.data.repository.ItemRepository
import com.tecsup.productos.data.repository.ItemRepositoryImpl
import com.tecsup.productos.notification.Notificador
import com.tecsup.productos.notification.NotificadorConsola

class AppContainer(context: Context) {

    companion object {
        /** Variante del grupo. Cambia a "laptops", "fragrances" o "groceries" según tu asignación. */
        const val CATEGORIA = "smartphones"
    }

    private val appContext = context.applicationContext

    private val baseDeDatos: AppDatabase by lazy {
        Room.databaseBuilder(appContext, AppDatabase::class.java, "productos.db").build()
    }

    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(
            api = RetrofitClient.api,
            dao = baseDeDatos.itemDao(),
            categoria = CATEGORIA
        )
    }

    val notificador: Notificador by lazy { NotificadorConsola() }
}