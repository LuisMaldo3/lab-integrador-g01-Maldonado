package com.tecsup.productos.ui.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tecsup.productos.data.mapper.formatearPrecio
import com.tecsup.productos.domain.model.Item
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.*
import com.tecsup.productos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AdminViewModel, onVolver: () -> Unit, onVerDetalle: (Int) -> Unit = {}) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val catalogo by viewModel.catalogo.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    val trabajando by viewModel.trabajando.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var confirmarBorrado by remember { mutableStateOf(false) }
    var busqueda by remember { mutableStateOf("") }
    LaunchedEffect(mensaje) { mensaje?.let { snackbar.showSnackbar(it); viewModel.limpiarMensaje() } }
    val marcas = remember(catalogo) {
        catalogo.groupBy { it.subtitulo.substringBefore(" · ").ifBlank { "Sin marca" } }
            .toList().sortedByDescending { it.second.size }
    }
    Scaffold(containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = { TopAppBar(title = { Text("Administración", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) }
    ) { pad ->
        when (val e = estado) {
            is UiState.Cargando -> Cargando(modifier = Modifier.padding(pad))
            is UiState.Error -> ErrorView(e.mensaje, viewModel::cargarTotales, Modifier.padding(pad))
            is UiState.Exito -> Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState())
                .padding(17.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.fillMaxWidth().background(HeroGradient, RoundedCornerShape(24.dp)).padding(21.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Dashboard, null, tint = Color.White)
                            Spacer(Modifier.width(9.dp))
                            Text("Tienda Tecsup", style = MaterialTheme.typography.titleLarge, color = Color.White)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("Panel de catálogo local y mantenimiento", color = Color(0xFFE1EEFF),
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Text("Resumen general", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminMetric("Guardados", e.datos.guardados, Icons.Default.Inventory2, Modifier.weight(1f))
                    AdminMetric("Favoritos", e.datos.favoritos, Icons.Default.Favorite, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminMetric("Marcas", marcas.size, Icons.Default.Sell, Modifier.weight(1f))
                    AdminMetric("En el dispositivo", catalogo.size, Icons.Default.PhoneAndroid, Modifier.weight(1f))
                }
                Text("Distribución por marcas", style = MaterialTheme.typography.titleLarge)
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (marcas.isEmpty()) Text("Sin datos locales. Sincroniza primero.", color = TextoSecundario)
                        marcas.take(8).forEach { (marca, productos) ->
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(marca, style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f), maxLines = 1)
                                Text("${productos.size} modelos", color = Azul, style = MaterialTheme.typography.labelLarge)
                            }
                            LinearProgressIndicator(progress = { if (catalogo.isEmpty()) 0f else productos.size.toFloat() / catalogo.size },
                                modifier = Modifier.fillMaxWidth(), color = Azul, trackColor = Suave)
                        }
                    }
                }
                Text("Herramientas de mantenimiento", style = MaterialTheme.typography.titleLarge)
                Card(shape = RoundedCornerShape(21.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, null, tint = Azul)
                            Spacer(Modifier.width(10.dp))
                            Text("Sincronizar catálogo", style = MaterialTheme.typography.titleMedium)
                        }
                        Text("Actualiza los registros locales desde la API pública.",
                            color = TextoSecundario, style = MaterialTheme.typography.bodyMedium)
                        BrandButton("Sincronizar ahora", viewModel::sincronizar, Modifier.fillMaxWidth(),
                            enabled = !trabajando, loading = trabajando)
                    }
                }
                Card(shape = RoundedCornerShape(21.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DeleteOutline, null, tint = Coral)
                            Spacer(Modifier.width(10.dp))
                            Text("Almacenamiento local", style = MaterialTheme.typography.titleMedium)
                        }
                        Text("Elimina la copia guardada en este teléfono. Esta operación también elimina los favoritos locales.",
                            color = TextoSecundario, style = MaterialTheme.typography.bodyMedium)
                        SoftButton("Limpiar caché", { confirmarBorrado = true }, Modifier.fillMaxWidth())
                    }
                }
                Text("Consultar inventario local", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(value = busqueda, onValueChange = { busqueda = it },
                    placeholder = { Text("Buscar en el catálogo guardado") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(17.dp), modifier = Modifier.fillMaxWidth())
                val encontrados = catalogo.filter { it.titulo.contains(busqueda, true) }.take(12)
                if (encontrados.isEmpty()) Text("No hay registros para mostrar", color = TextoSecundario)
                encontrados.forEach { item ->
                    Card(onClick = { onVerDetalle(item.id) }, shape = RoundedCornerShape(17.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(item.imagenUrl, item.titulo, Modifier.size(59.dp))
                            Spacer(Modifier.width(11.dp))
                            Column(Modifier.weight(1f)) {
                                Text(item.titulo, style = MaterialTheme.typography.labelLarge, maxLines = 2)
                                Text(formatearPrecio(item.precio), color = Azul)
                            }
                            Icon(Icons.Default.ChevronRight, "Ver detalle", tint = TextoSecundario)
                        }
                    }
                }
                Text("La fuente de productos es de solo lectura. Este panel no modifica precios ni stock remoto.",
                    color = TextoSecundario, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(10.dp))
            }
        }
    }
    if (confirmarBorrado) AlertDialog(onDismissRequest = { confirmarBorrado = false },
        title = { Text("¿Limpiar caché del dispositivo?") },
        text = { Text("Esto eliminará los productos guardados y sus favoritos locales. Puedes sincronizar otra vez.") },
        confirmButton = { TextButton(onClick = { confirmarBorrado = false; viewModel.limpiarCache() }) { Text("Eliminar") } },
        dismissButton = { TextButton(onClick = { confirmarBorrado = false }) { Text("Cancelar") } })
}

@Composable
private fun AdminMetric(titulo: String, numero: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.fillMaxWidth().padding(15.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Icon(icon, null, tint = Azul, modifier = Modifier.size(28.dp))
            Text(numero.toString(), style = MaterialTheme.typography.headlineMedium, color = Azul)
            Text(titulo, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
        }
    }
}
