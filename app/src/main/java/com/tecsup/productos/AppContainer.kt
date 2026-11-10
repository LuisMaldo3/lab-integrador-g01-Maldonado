package com.tecsup.productos

import android.content.Context
import androidx.room.Room
import com.tecsup.productos.data.auth.AuthRepository
import com.tecsup.productos.data.auth.FirebaseAuthRepository
import com.tecsup.productos.data.local.AppDatabase
import com.tecsup.productos.data.remote.RetrofitClient
import com.tecsup.productos.data.repository.ItemRepository
import com.tecsup.productos.data.repository.ItemRepositoryImpl
import com.tecsup.productos.notification.Notificador
import com.tecsup.productos.data.preferences.PreferenciasRepository
import com.tecsup.productos.data.preferences.PreferenciasLocales
import com.tecsup.productos.notification.NotificadorAndroid

/**
 * Contenedor de dependencias "a mano": crea API, base de datos y repositorios UNA sola vez.
 * Los ViewModel reciben las interfaces desde aquí a través de ViewModelFactory.
 */
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

    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }

    val preferencias: PreferenciasRepository by lazy { PreferenciasLocales(appContext) }

    val notificador: Notificador by lazy { NotificadorAndroid(appContext) }
}