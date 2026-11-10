package com.tecsup.productos.ui.lista

import com.tecsup.productos.ReglaDispatcherPrincipal
import com.tecsup.productos.domain.model.Item
import com.tecsup.productos.fakes.FakeItemRepository
import com.tecsup.productos.fakes.FakeNotificador
import com.tecsup.productos.ui.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {

    @get:Rule
    val regla = ReglaDispatcherPrincipal()

    private fun item(id: Int, titulo: String, precio: Double, fav: Boolean = false) =
        Item(id = id, titulo = titulo, subtitulo = "$ $precio", imagenUrl = null, precio = precio, esFavorito = fav)

    private val iphone = item(1, "iPhone 9", 549.0)
    private val samsung = item(3, "Samsung Universe 9", 1249.0)

    @Test
    fun `con internet la lista pasa de Cargando a Exito con los datos`() = runTest {
        val repo = FakeItemRepository(remotos = listOf(iphone, samsung))
        val vm = ListaViewModel(repo, FakeNotificador())

        val estado = vm.estado.value
        assertTrue(estado is UiState.Exito)
        assertEquals(listOf(iphone, samsung), (estado as UiState.Exito).datos)
        assertNull(vm.aviso.value)
    }

    @Test
    fun `sin internet y sin datos guardados muestra Error`() = runTest {
        val repo = FakeItemRepository(falla = IOException("sin red"))
        val vm = ListaViewModel(repo, FakeNotificador())

        val estado = vm.estado.value
        assertTrue(estado is UiState.Error)
        assertEquals("No hay conexión a internet", (estado as UiState.Error).mensaje)
    }

    @Test
    fun `sin internet pero con datos guardados muestra la lista y un aviso`() = runTest {
        val repo = FakeItemRepository(
            guardadosAlInicio = listOf(iphone),
            falla = IOException("sin red")
        )
        val vm = ListaViewModel(repo, FakeNotificador())

        assertTrue(vm.estado.value is UiState.Exito)
        assertEquals(listOf(iphone), (vm.estado.value as UiState.Exito).datos)
        assertEquals("Sin conexión: mostrando datos guardados", vm.aviso.value)
    }

    @Test
    fun `al sincronizar con exito se pide la notificacion con la cantidad`() = runTest {
        val notificador = FakeNotificador()
        val repo = FakeItemRepository(remotos = listOf(iphone, samsung))
        ListaViewModel(repo, notificador)

        assertEquals(1, notificador.mostradas.size)
        assertEquals("Se guardaron 2 elementos", notificador.mostradas.first().second)
    }

    @Test
    fun `alternarFavorito cambia el favorito y se conserva al volver a sincronizar`() = runTest {
        val repo = FakeItemRepository(remotos = listOf(iphone, samsung))
        val vm = ListaViewModel(repo, FakeNotificador())

        vm.alternarFavorito(3)
        var lista = (vm.estado.value as UiState.Exito).datos
        assertTrue(lista.first { it.id == 3 }.esFavorito)

        vm.refrescar()
        lista = (vm.estado.value as UiState.Exito).datos
        assertTrue("el favorito no debe perderse al sincronizar", lista.first { it.id == 3 }.esFavorito)
        assertEquals(2, repo.vecesRefrescado)
    }

    @Test
    fun `reintentar desde Error vuelve a Exito cuando regresa la conexion`() = runTest {
        val repo = FakeItemRepository(remotos = listOf(iphone), falla = IOException("sin red"))
        val vm = ListaViewModel(repo, FakeNotificador())
        assertTrue(vm.estado.value is UiState.Error)

        repo.falla = null          // "vuelve" el internet
        vm.refrescar()

        assertTrue(vm.estado.value is UiState.Exito)
        assertEquals(1, (vm.estado.value as UiState.Exito).datos.size)
    }
}