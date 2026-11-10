package com.tecsup.productos.ui.favoritos

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.*
import com.tecsup.productos.ui.lista.ItemCard
import com.tecsup.productos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    viewModel: FavoritosViewModel,
    onItemClick: (Int) -> Unit,
    onVolver: () -> Unit,
    onComparar: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }
    var menorPrimero by rememberSaveable { mutableStateOf(false) }
    Scaffold(containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(title = { Text("Mis favoritos", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
            actions = { IconButton(onClick = onComparar) { Icon(Icons.Default.CompareArrows, "Comparar favoritos") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)) }
    ) { pad ->
        when (val e = estado) {
            is UiState.Cargando -> Cargando(modifier = Modifier.padding(pad))
            is UiState.Error -> ErrorView(e.mensaje, onVolver, Modifier.padding(pad), "Volver al catálogo")
            is UiState.Exito -> {
                if (e.datos.isEmpty()) {
                    Column(Modifier.fillMaxSize().padding(pad).padding(28.dp),
                        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FavoriteBorder, null, tint = Azul, modifier = Modifier.size(90.dp))
                        Spacer(Modifier.height(15.dp))
                        Text("Tu colección comienza aquí", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(7.dp))
                        Text("Guarda los modelos que te interesan para volver a verlos cuando quieras.",
                            color = TextoSecundario, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(24.dp))
                        BrandButton("Explorar teléfonos", onVolver, Modifier.fillMaxWidth())
                    }
                } else {
                    val filtrados = e.datos.filter { it.titulo.contains(query, true) || it.subtitulo.contains(query, true) }
                        .let { if (menorPrimero) it.sortedBy { p -> p.precio } else it.sortedBy { p -> p.titulo } }
                    LazyVerticalGrid(columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().padding(pad),
                        contentPadding = PaddingValues(15.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(13.dp)) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                                Text("Tus smartphones guardados", style = MaterialTheme.typography.titleLarge)
                                Text("${e.datos.size} productos favoritos", color = TextoSecundario)
                                OutlinedTextField(query, { query = it }, modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Buscar en favoritos") },
                                    leadingIcon = { Icon(Icons.Default.Search, null) },
                                    singleLine = true, shape = RoundedCornerShape(16.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                    FilterChip(selected = menorPrimero, onClick = { menorPrimero = !menorPrimero },
                                        label = { Text(if (menorPrimero) "Precio menor primero" else "Orden alfabético") },
                                        leadingIcon = { Icon(Icons.Default.Sort, null, Modifier.size(17.dp)) })
                                    Spacer(Modifier.width(9.dp))
                                    Text("${filtrados.size} resultados", style = MaterialTheme.typography.labelMedium, color = Azul)
                                }
                            }
                        }
                        if (filtrados.isEmpty()) item(span = { GridItemSpan(maxLineSpan) }) {
                            Column(Modifier.fillMaxWidth().padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No hay favoritos con esa búsqueda", color = TextoSecundario)
                                TextButton(onClick = { query = "" }) { Text("Limpiar búsqueda") }
                            }
                        }
                        items(filtrados, key = { it.id }) { p ->
                            ItemCard(p, onClick = { onItemClick(p.id) },
                                onFavorito = { viewModel.alternarFavorito(p.id) })
                        }
                    }
                }
            }
        }
    }
}
