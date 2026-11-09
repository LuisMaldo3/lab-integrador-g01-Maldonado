package com.tecsup.productos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.tecsup.productos.AppContainer
import com.tecsup.productos.ui.detalle.DetalleViewModel
import com.tecsup.productos.ui.favoritos.FavoritosViewModel
import com.tecsup.productos.ui.lista.ListaViewModel
import com.tecsup.productos.ui.login.LoginViewModel

/** Fábrica única: recibe el AppContainer y entrega a cada ViewModel sus interfaces. */
class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = when {
        modelClass.isAssignableFrom(ListaViewModel::class.java) ->
            ListaViewModel(container.itemRepository, container.notificador)

        modelClass.isAssignableFrom(DetalleViewModel::class.java) ->
            // SavedStateHandle trae el argumento "id" de la ruta detalle/{id}
            DetalleViewModel(container.itemRepository, extras.createSavedStateHandle())

        modelClass.isAssignableFrom(FavoritosViewModel::class.java) ->
            FavoritosViewModel(container.itemRepository)

        modelClass.isAssignableFrom(LoginViewModel::class.java) ->
            LoginViewModel(container.authRepository)

        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    } as T
}