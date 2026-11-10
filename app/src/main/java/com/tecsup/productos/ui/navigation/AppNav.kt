package com.tecsup.productos.ui.navigation

import android.net.Uri
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
import com.tecsup.productos.data.preferences.ModoTema
import com.tecsup.productos.domain.model.Rol
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.ViewModelFactory
import com.tecsup.productos.ui.admin.AdminScreen
import com.tecsup.productos.ui.admin.AdminViewModel
import com.tecsup.productos.ui.comparador.CompararScreen
import com.tecsup.productos.ui.comparador.CompararViewModel
import com.tecsup.productos.ui.components.Cargando
import com.tecsup.productos.ui.detalle.DetalleScreen
import com.tecsup.productos.ui.detalle.DetalleViewModel
import com.tecsup.productos.ui.favoritos.FavoritosScreen
import com.tecsup.productos.ui.favoritos.FavoritosViewModel
import com.tecsup.productos.ui.lista.ListaScreen
import com.tecsup.productos.ui.lista.ListaViewModel
import com.tecsup.productos.ui.login.*
import com.tecsup.productos.ui.perfil.PerfilScreen

object Ruta {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val CUENTA_CREADA = "cuenta_creada/{correo}"
    const val RECUPERAR = "recuperar"
    const val RECUPERAR_EXITO = "recuperar_exito"
    const val LISTA = "lista"
    const val DETALLE = "detalle/{id}"
    const val FAVORITOS = "favoritos"
    const val ADMIN = "admin"
    const val PERFIL = "perfil"
    const val COMPARAR = "comparar?inicial={inicial}"
    fun detalle(id: Int) = "detalle/$id"
    fun comparar(id: Int? = null) = "comparar?inicial=${id ?: -1}"
    fun cuentaCreada(correo: String) = "cuenta_creada/${Uri.encode(correo)}"
}

@Composable
fun AppNav(
    container: AppContainer,
    modo: ModoTema = ModoTema.SISTEMA,
    onCambiarModo: (ModoTema) -> Unit = {}
) {
    val factory = remember { ViewModelFactory(container) }
    val loginVm: LoginViewModel = viewModel(factory = factory)
    val listo by loginVm.listo.collectAsStateWithLifecycle()
    val estadoSesion by loginVm.estado.collectAsStateWithLifecycle()
    if (!listo) { Cargando("Verificando sesión…"); return }
    val sesion = (estadoSesion as? UiState.Exito)?.datos
    val nav = rememberNavController()
    val inicio = remember { if (sesion != null) Ruta.LISTA else Ruta.LOGIN }
    LaunchedEffect(sesion) {
        val actual = nav.currentDestination?.route
        if (sesion != null && actual == Ruta.LOGIN) {
            nav.navigate(Ruta.LISTA) { popUpTo(Ruta.LOGIN) { inclusive = true } }
        } else if (sesion == null && actual in listOf(
                Ruta.LISTA, Ruta.DETALLE, Ruta.FAVORITOS, Ruta.ADMIN, Ruta.PERFIL, Ruta.COMPARAR
            )) {
            nav.navigate(Ruta.LOGIN) { popUpTo(0) { inclusive = true } }
        }
    }
    NavHost(navController = nav, startDestination = inicio) {
        composable(Ruta.LOGIN) { back ->
            val correo by back.savedStateHandle.getStateFlow("prefill_correo", "").collectAsStateWithLifecycle()
            LoginScreen(loginVm, onCrearCuenta = { nav.navigate(Ruta.REGISTRO) },
                onRecuperar = { nav.navigate(Ruta.RECUPERAR) }, correoPrecargado = correo)
        }
        composable(Ruta.REGISTRO) {
            val vm: RegisterViewModel = viewModel(factory = factory)
            RegisterScreen(vm, onVolver = { nav.popBackStack() }, onCreado = { email ->
                nav.navigate(Ruta.cuentaCreada(email)) { popUpTo(Ruta.REGISTRO) { inclusive = true } }
            })
        }
        composable(Ruta.CUENTA_CREADA, arguments = listOf(navArgument("correo") { type = NavType.StringType })) { back ->
            val correo = Uri.decode(back.arguments?.getString("correo") ?: "")
            AccountCreatedScreen(correo, onIrALogin = {
                nav.getBackStackEntry(Ruta.LOGIN).savedStateHandle["prefill_correo"] = correo
                nav.popBackStack(Ruta.LOGIN, false)
            })
        }
        composable(Ruta.RECUPERAR) {
            val vm: RecuperarPasswordViewModel = viewModel(factory = factory)
            RecuperarPasswordScreen(vm, onVolver = { nav.popBackStack() }, onEnviado = {
                nav.navigate(Ruta.RECUPERAR_EXITO) { popUpTo(Ruta.RECUPERAR) { inclusive = true } }
            })
        }
        composable(Ruta.RECUPERAR_EXITO) {
            RecuperarPasswordExitoScreen(onVolver = { nav.popBackStack(Ruta.LOGIN, false) })
        }
        composable(Ruta.LISTA) {
            val vm: ListaViewModel = viewModel(factory = factory)
            ListaScreen(viewModel = vm,
                onItemClick = { nav.navigate(Ruta.detalle(it)) },
                esAdmin = sesion?.rol == Rol.ADMIN,
                onIrAFavoritos = { nav.navigate(Ruta.FAVORITOS) },
                onIrAAdmin = { nav.navigate(Ruta.ADMIN) },
                onIrAComparar = { nav.navigate(Ruta.comparar()) },
                onIrACuenta = { nav.navigate(Ruta.PERFIL) },
                onCerrarSesion = loginVm::cerrarSesion)
        }
        composable(Ruta.PERFIL) {
            if (sesion != null) {
                PerfilScreen(sesion, onVolver = { nav.popBackStack() },
                    modo = modo, onCambiarModo = onCambiarModo,
                    onFavoritos = { nav.navigate(Ruta.FAVORITOS) },
                    onComparar = { nav.navigate(Ruta.comparar()) },
                    onAdmin = { nav.navigate(Ruta.ADMIN) },
                    onCerrarSesion = loginVm::cerrarSesion)
            } else LaunchedEffect(Unit) { nav.navigate(Ruta.LOGIN) }
        }
        composable(Ruta.COMPARAR, arguments = listOf(navArgument("inicial") {
            type = NavType.IntType; defaultValue = -1
        })) { entry ->
            val vm: CompararViewModel = viewModel(factory = factory)
            val primerId = entry.arguments?.getInt("inicial")?.takeIf { it >= 0 }
            CompararScreen(vm, onVolver = { nav.popBackStack() },
                onDetalle = { nav.navigate(Ruta.detalle(it)) }, inicial = primerId)
        }
        composable(Ruta.ADMIN) {
            if (sesion?.rol == Rol.ADMIN) {
                val vm: AdminViewModel = viewModel(factory = factory)
                AdminScreen(vm, onVolver = { nav.popBackStack() },
                    onVerDetalle = { nav.navigate(Ruta.detalle(it)) })
            } else LaunchedEffect(Unit) { nav.popBackStack() }
        }
        composable(Ruta.FAVORITOS) {
            val vm: FavoritosViewModel = viewModel(factory = factory)
            FavoritosScreen(vm, onItemClick = { nav.navigate(Ruta.detalle(it)) },
                onVolver = { nav.popBackStack() },
                onComparar = { nav.navigate(Ruta.comparar()) })
        }
        composable(Ruta.DETALLE, arguments = listOf(navArgument("id") { type = NavType.IntType })) {
            val vm: DetalleViewModel = viewModel(factory = factory)
            DetalleScreen(vm, onVolver = { nav.popBackStack() },
                onComparar = { nav.navigate(Ruta.comparar(it)) },
                onRelacionado = { nav.navigate(Ruta.detalle(it)) })
        }
    }
}
