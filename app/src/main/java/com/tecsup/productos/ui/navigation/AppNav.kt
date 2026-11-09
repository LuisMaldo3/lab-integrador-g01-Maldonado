package com.tecsup.productos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.productos.AppContainer
import com.tecsup.productos.domain.model.Rol
import com.tecsup.productos.ui.admin.AdminScreen
import com.tecsup.productos.ui.admin.AdminViewModel
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.ViewModelFactory
import com.tecsup.productos.ui.components.Cargando
import com.tecsup.productos.ui.detalle.DetalleScreen
import com.tecsup.productos.ui.detalle.DetalleViewModel
import com.tecsup.productos.ui.favoritos.FavoritosScreen
import com.tecsup.productos.ui.favoritos.FavoritosViewModel
import com.tecsup.productos.ui.lista.ListaScreen
import com.tecsup.productos.ui.lista.ListaViewModel
import com.tecsup.productos.ui.login.LoginScreen
import com.tecsup.productos.ui.login.LoginViewModel

object Ruta {
    const val LOGIN = "login"
    const val LISTA = "lista"
    const val DETALLE = "detalle/{id}"
    const val FAVORITOS = "favoritos"
    const val ADMIN = "admin"
    fun detalle(id: Int) = "detalle/$id"
}

@Composable
fun AppNav(container: AppContainer) {
    val factory = remember { ViewModelFactory(container) }

    // El LoginViewModel se crea AQUÍ (fuera del NavHost): vive a nivel de Activity
    // y guarda la sesión para todas las pantallas.
    val loginVm: LoginViewModel = viewModel(factory = factory)
    val listo by loginVm.listo.collectAsStateWithLifecycle()
    val estadoSesion by loginVm.estado.collectAsStateWithLifecycle()

    if (!listo) {
        Cargando("Verificando sesión…")
        return
    }

    val sesion = (estadoSesion as? UiState.Exito)?.datos
    val nav = rememberNavController()
    val inicio = remember { if (sesion != null) Ruta.LISTA else Ruta.LOGIN }

    // Reacciona a entrar / salir de la sesión
    LaunchedEffect(sesion) {
        val actual = nav.currentDestination?.route
        if (sesion != null && actual == Ruta.LOGIN) {
            nav.navigate(Ruta.LISTA) { popUpTo(Ruta.LOGIN) { inclusive = true } }
        }
        if (sesion == null && actual != null && actual != Ruta.LOGIN) {
            nav.navigate(Ruta.LOGIN) { popUpTo(0) { inclusive = true } }
        }
    }

    NavHost(navController = nav, startDestination = inicio) {

        composable(Ruta.LOGIN) {
            LoginScreen(viewModel = loginVm)
        }

        composable(Ruta.LISTA) {
            val vm: ListaViewModel = viewModel(factory = factory)
            ListaScreen(
                viewModel = vm,
                onItemClick = { id -> nav.navigate(Ruta.detalle(id)) },
                esAdmin = sesion?.rol == Rol.ADMIN,
                onIrAFavoritos = { nav.navigate(Ruta.FAVORITOS) },
                onIrAAdmin = { nav.navigate(Ruta.ADMIN) },
                onCerrarSesion = loginVm::cerrarSesion
            )
        }

        composable(Ruta.ADMIN) {
            // Segunda barrera: aunque alguien llegue a la ruta, sin rol ADMIN no ve el panel
            if (sesion?.rol == Rol.ADMIN) {
                val vm: AdminViewModel = viewModel(factory = factory)
                AdminScreen(viewModel = vm, onVolver = { nav.popBackStack() })
            } else {
                LaunchedEffect(Unit) { nav.popBackStack() }
            }
        }

        composable(Ruta.FAVORITOS) {
            val vm: FavoritosViewModel = viewModel(factory = factory)
            FavoritosScreen(
                viewModel = vm,
                onItemClick = { id -> nav.navigate(Ruta.detalle(id)) },
                onVolver = { nav.popBackStack() }
            )
        }

        composable(
            route = Ruta.DETALLE,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            val vm: DetalleViewModel = viewModel(factory = factory)
            DetalleScreen(viewModel = vm, onVolver = { nav.popBackStack() })
        }
    }
}