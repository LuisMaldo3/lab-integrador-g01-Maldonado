package com.tecsup.productos.ui.comparador

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tecsup.productos.data.mapper.formatearPrecio
import com.tecsup.productos.domain.model.Item
import com.tecsup.productos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompararScreen(
    vm: CompararViewModel,
    onVolver: () -> Unit,
    onDetalle: (Int) -> Unit,
    inicial: Int? = null
) {
    val productos by vm.productos.collectAsStateWithLifecycle()
    val seleccionados by vm.seleccion.collectAsStateWithLifecycle()
    LaunchedEffect(inicial) { inicial?.let(vm::agregar) }
    var busqueda by remember { mutableStateOf("") }
    val elegidos = productos.filter { it.id in seleccionados }.sortedBy { seleccionados.indexOf(it.id) }
    val lista = productos.filter { it.titulo.contains(busqueda, true) || (it.marca ?: it.subtitulo).contains(busqueda, true) }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(
            title = { Text("Comparar smartphones", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        ) }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(pad),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(23.dp)).background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(Marino, Azul, Violeta))
                ).padding(19.dp)) {
                    Column {
                        Text("Encuentra el modelo perfecto", color = Color.White, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(6.dp))
                        Text("Selecciona 2 o 3 teléfonos para comparar sus datos reales.", color = Color(0xFFE5F1FF), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            item {
                OutlinedTextField(value = busqueda, onValueChange = { busqueda = it },
                    label = { Text("Buscar teléfono para comparar") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    shape = RoundedCornerShape(17.dp))
            }
            item {
                Text("Seleccionados (${elegidos.size}/3)", style = MaterialTheme.typography.titleMedium)
                if (elegidos.isEmpty()) {
                    Text("Elige modelos de la lista inferior.", color = TextoSecundario)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        elegidos.forEach { item ->
                            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surface).padding(9.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(item.imagenUrl, null, modifier = Modifier.size(52.dp), contentScale = ContentScale.Fit)
                                Spacer(Modifier.width(9.dp))
                                Text(item.titulo, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, maxLines = 2)
                                IconButton(onClick = { vm.quitar(item.id) }) {
                                    Icon(Icons.Default.Close, "Quitar ${item.titulo}", tint = Coral)
                                }
                            }
                        }
                    }
                    TextButton(onClick = vm::vaciar) { Text("Vaciar comparación") }
                }
            }
            if (elegidos.size >= 2) {
                item {
                    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            Text("Comparación lado a lado", style = MaterialTheme.typography.titleLarge)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                elegidos.forEach { p ->
                                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                        AsyncImage(p.imagenUrl, p.titulo, Modifier.fillMaxWidth().height(95.dp), contentScale = ContentScale.Fit)
                                        Text(p.titulo, style = MaterialTheme.typography.labelMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        Text(formatearPrecio(p.precio), color = Azul, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            HorizontalDivider()
                            ComparisonRow("Marca", elegidos) { it.marca ?: it.subtitulo.substringBefore(" · ") }
                            ComparisonRow("Categoría", elegidos) { it.categoria ?: "No informada" }
                            ComparisonRow("Precio", elegidos) { formatearPrecio(it.precio) }
                            ComparisonRow("Calificación", elegidos) { it.calificacion?.let { c -> "%.1f / 5".format(c) } ?: "No informada" }
                            ComparisonRow("Disponibilidad", elegidos) { it.stock?.let { n -> if (n > 0) "$n unidades" else "Agotado" } ?: "No informada" }
                            Text("Solo se comparan los datos disponibles en el catálogo. No se inventan especificaciones.",
                                style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                        }
                    }
                }
            }
            item { Text("Añadir a la comparación", style = MaterialTheme.typography.titleLarge) }
            if (productos.isEmpty()) item {
                Text("Aún no hay datos guardados. Abre Inicio y sincroniza el catálogo.", color = TextoSecundario)
            }
            items(lista, key = { it.id }) { item ->
                val elegido = item.id in seleccionados
                Card(shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(Modifier.fillMaxWidth().clickable {
                        vm.alternar(item.id)
                    }.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(item.imagenUrl, item.titulo, Modifier.size(68.dp), contentScale = ContentScale.Fit)
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(item.titulo, style = MaterialTheme.typography.labelLarge, maxLines = 2)
                            Text(formatearPrecio(item.precio), color = Azul, style = MaterialTheme.typography.titleMedium)
                            Text("Ver ficha", modifier = Modifier.clickable { onDetalle(item.id) }, style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
                        }
                        Checkbox(checked = elegido, onCheckedChange = {
                            if (it) vm.agregar(item.id) else vm.quitar(item.id)
                        }, enabled = elegido || seleccionados.size < 3)
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonRow(titulo: String, items: List<Item>, dato: (Item) -> String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(titulo, style = MaterialTheme.typography.labelLarge, color = TextoSecundario)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { item ->
                Box(Modifier.weight(1f).background(Suave, RoundedCornerShape(12.dp)).padding(9.dp)) {
                    Text(dato(item), style = MaterialTheme.typography.labelMedium, color = Marino, maxLines = 3)
                }
            }
        }
    }
}
