package com.tecsup.productos.ui.lista

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.tecsup.productos.data.mapper.formatearPrecio
import com.tecsup.productos.domain.model.Item
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.*
import com.tecsup.productos.ui.theme.*
import kotlinx.coroutines.launch

private enum class Orden(val texto: String) {
    DESTACADOS("Destacados"), PRECIO_MENOR("Menor precio"),
    PRECIO_MAYOR("Mayor precio"), ALFABETICO("Nombre A-Z")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    viewModel: ListaViewModel,
    onItemClick: (Int) -> Unit = {},
    esAdmin: Boolean = false,
    onIrAFavoritos: () -> Unit = {},
    onIrAAdmin: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onIrAComparar: () -> Unit = {},
    onIrACuenta: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val aviso by viewModel.aviso.collectAsStateWithLifecycle()
    var busqueda by rememberSaveable { mutableStateOf("") }
    var marca by rememberSaveable { mutableStateOf("Todos") }
    var orden by rememberSaveable { mutableStateOf(Orden.DESTACADOS) }
    var topePrecio by rememberSaveable { mutableFloatStateOf(Float.POSITIVE_INFINITY) }
    var mostrarFiltros by remember { mutableStateOf(false) }
    var mostrarAsistente by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val productos = (estado as? UiState.Exito)?.datos.orEmpty()
    val marcas = remember(productos) {
        listOf("Todos") + productos.map { marcaDe(it) }.filter { it.isNotBlank() }.distinct().sorted()
    }
    val precioMayor = remember(productos) { (productos.maxOfOrNull { it.precio } ?: 1.0).toFloat().coerceAtLeast(1f) }
    val filtrados = remember(productos, busqueda, marca, orden, topePrecio) {
        val f = productos.filter { p ->
            (marca == "Todos" || marcaDe(p).equals(marca, true)) &&
                (busqueda.isBlank() || p.titulo.contains(busqueda, true) || marcaDe(p).contains(busqueda, true)) &&
                p.precio <= topePrecio.toDouble()
        }
        when (orden) {
            Orden.PRECIO_MENOR -> f.sortedBy { it.precio }
            Orden.PRECIO_MAYOR -> f.sortedByDescending { it.precio }
            Orden.ALFABETICO -> f.sortedBy { it.titulo.lowercase() }
            Orden.DESTACADOS -> f.sortedWith(compareByDescending<Item> { (it.descuento ?: 0.0) > 0.0 }
                .thenBy { it.titulo })
        }
    }
    fun limpiarFiltros() { busqueda = ""; marca = "Todos"; orden = Orden.DESTACADOS; topePrecio = Float.POSITIVE_INFINITY }

    Scaffold(containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            ShopBottomBar(onInicio = { scope.launch { gridState.animateScrollToItem(0) } },
                onComparar = onIrAComparar, onFavoritos = onIrAFavoritos,
                onCuenta = onIrACuenta)
        }) { pad ->
        when (val e = estado) {
            is UiState.Cargando -> Cargando(modifier = Modifier.padding(pad))
            is UiState.Error -> ErrorView(e.mensaje, viewModel::refrescar, Modifier.padding(pad))
            is UiState.Exito -> LazyVerticalGrid(
                columns = GridCells.Fixed(2), state = gridState,
                modifier = Modifier.fillMaxSize().padding(pad),
                contentPadding = PaddingValues(start = 15.dp, end = 15.dp, bottom = 26.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column {
                        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                            .background(HeroGradient).padding(start = 17.dp, end = 10.dp, top = 11.dp, bottom = 22.dp)) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BrandLogo(Modifier.width(142.dp).height(55.dp))
                                    Spacer(Modifier.weight(1f))
                                    IconButton(onClick = onIrAFavoritos) { Icon(Icons.Default.FavoriteBorder, "Favoritos", tint = Color.White) }
                                    IconButton(onClick = onIrACuenta) { Icon(Icons.Default.AccountCircle, "Mi cuenta", tint = Color.White) }
                                    if (esAdmin) IconButton(onClick = onIrAAdmin) {
                                        Icon(Icons.Default.AdminPanelSettings, "Panel Admin", tint = Color.White)
                                    }
                                }
                                Spacer(Modifier.height(5.dp))
                                Text("Descubre tu próximo smartphone", color = Color.White,
                                    style = MaterialTheme.typography.titleLarge)
                                Text("Grandes modelos, una experiencia única", color = Color(0xFFE3F1FF),
                                    style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        BannerCarousel(onExplorar = { scope.launch { gridState.animateScrollToItem(1) } })
                        Spacer(Modifier.height(18.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.weight(1f)) {
                                AuthField(busqueda, { busqueda = it }, "Buscar modelo o marca", Icons.Default.Search)
                            }
                            IconButton(onClick = { mostrarFiltros = true }, modifier = Modifier.size(52.dp)
                                .background(Azul, RoundedCornerShape(16.dp))) {
                                Icon(Icons.Default.Tune, "Abrir filtros y ordenamiento", tint = Color.White)
                            }
                        }
                        aviso?.let { Spacer(Modifier.height(10.dp)); AvisoSinConexion(it) }
                        Spacer(Modifier.height(14.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(marcas) { m -> ColorChip(m, marca == m, { marca = m }) }
                        }
                        Spacer(Modifier.height(14.dp))
                        Card(onClick = { mostrarAsistente = true }, shape = RoundedCornerShape(19.dp),
                            colors = CardDefaults.cardColors(containerColor = Suave), modifier = Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, null, tint = Azul, modifier = Modifier.size(28.dp))
                                Spacer(Modifier.width(11.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("Encuentra tu smartphone ideal", style = MaterialTheme.typography.titleMedium, color = Marino)
                                    Text("Te ayudamos a filtrar por presupuesto y marca", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                                }
                                Icon(Icons.Default.ChevronRight, "Abrir guía", tint = Azul)
                            }
                        }
                        Spacer(Modifier.height(18.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Smartphones destacados", style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(1f), maxLines = 2)
                            Text("${filtrados.size} modelos", color = Azul, style = MaterialTheme.typography.labelMedium)
                        }
                        Text("Orden: ${orden.texto}", color = TextoSecundario, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (e.datos.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ErrorView("Aún no hay productos. Sincroniza el catálogo.", viewModel::refrescar,
                            textoBoton = "Sincronizar")
                    }
                } else if (filtrados.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(Modifier.fillMaxWidth().padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Search, null, tint = Azul, modifier = Modifier.size(46.dp))
                            Text("No encontramos resultados para estos filtros", color = TextoSecundario)
                            TextButton(onClick = ::limpiarFiltros) { Text("Limpiar filtros") }
                        }
                    }
                } else {
                    items(filtrados, key = { it.id }) { p ->
                        ItemCard(p, onClick = { onItemClick(p.id) }, onFavorito = { viewModel.alternarFavorito(p.id) })
                    }
                }
            }
        }
    }
    if (mostrarFiltros) ModalBottomSheet(onDismissRequest = { mostrarFiltros = false },
        containerColor = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 21.dp).padding(bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Filtrar y ordenar", style = MaterialTheme.typography.titleLarge)
            Text("Ordenar por", style = MaterialTheme.typography.titleMedium)
            Orden.entries.forEach { op ->
                Row(Modifier.fillMaxWidth().clickable { orden = op }.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = orden == op, onClick = { orden = op })
                    Text(op.texto)
                }
            }
            val limiteVisible = if (topePrecio.isFinite()) topePrecio.coerceIn(0f, precioMayor) else precioMayor
            Text("Precio máximo: ${if (topePrecio.isFinite()) formatearPrecio(limiteVisible.toDouble()) else "Sin límite"}",
                style = MaterialTheme.typography.titleMedium)
            Slider(value = limiteVisible, onValueChange = { topePrecio = it }, valueRange = 0f..precioMayor)
            TextButton(onClick = ::limpiarFiltros) { Text("Quitar todos los filtros") }
            Button(onClick = { mostrarFiltros = false }, modifier = Modifier.fillMaxWidth().height(53.dp),
                shape = RoundedCornerShape(17.dp)) { Text("Mostrar ${filtrados.size} modelos") }
        }
    }
    if (mostrarAsistente) ModalBottomSheet(onDismissRequest = { mostrarAsistente = false },
        containerColor = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Encuentra tu smartphone", style = MaterialTheme.typography.titleLarge)
            Text("Selecciona un presupuesto. Usaremos solamente modelos y precios reales del catálogo.",
                color = TextoSecundario, style = MaterialTheme.typography.bodyMedium)
            OutlinedButton(onClick = { topePrecio = precioMayor * .33f }, modifier = Modifier.fillMaxWidth()) {
                Text("Precio económico · hasta ${formatearPrecio((precioMayor * .33f).toDouble())}")
            }
            OutlinedButton(onClick = { topePrecio = precioMayor * .67f }, modifier = Modifier.fillMaxWidth()) {
                Text("Precio intermedio · hasta ${formatearPrecio((precioMayor * .67f).toDouble())}")
            }
            OutlinedButton(onClick = { topePrecio = Float.POSITIVE_INFINITY }, modifier = Modifier.fillMaxWidth()) {
                Text("Mostrar todos los precios")
            }
            Text("También puedes seleccionar una marca desde el catálogo.",
                style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
            Button(onClick = { mostrarAsistente = false; scope.launch { gridState.animateScrollToItem(1) } },
                modifier = Modifier.fillMaxWidth().height(53.dp), shape = RoundedCornerShape(17.dp)) {
                Text("Ver ${filtrados.size} modelos recomendados")
            }
        }
    }
}

private fun marcaDe(item: Item) = item.marca?.takeIf { it.isNotBlank() }
    ?: item.subtitulo.substringBefore(" · ").trim().takeIf { it.isNotBlank() } ?: ""

@Composable
private fun AvisoSinConexion(texto: String) {
    Row(Modifier.fillMaxWidth().background(Suave, RoundedCornerShape(13.dp)).padding(11.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Warning, null, tint = Azul, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(9.dp))
        Text(texto, color = Marino, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ItemCard(item: Item, onClick: () -> Unit, onFavorito: () -> Unit, mostrarFavorito: Boolean = true) {
    Card(onClick = onClick, shape = RoundedCornerShape(21.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(10.dp)) {
            Box(Modifier.fillMaxWidth().height(163.dp).clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF3F7FF))) {
                SubcomposeAsyncImage(model = item.imagenUrl, contentDescription = item.titulo,
                    contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
                        is AsyncImagePainter.State.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                        }
                        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Smartphone, "Sin fotografía", tint = Azul, modifier = Modifier.size(48.dp))
                        }
                    }
                }
                item.descuento?.takeIf { it > 0 }?.let { d ->
                    Box(Modifier.align(Alignment.TopStart).padding(6.dp).background(Coral, RoundedCornerShape(10.dp))
                        .padding(horizontal = 7.dp, vertical = 5.dp)) {
                        Text("-${d.toInt()}%", color = Color.White, style = MaterialTheme.typography.labelMedium)
                    }
                }
                if (mostrarFavorito) IconButton(onClick = onFavorito,
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                        .background(Color.White, CircleShape).size(43.dp)) {
                    Icon(if (item.esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        if (item.esFavorito) "Quitar de favoritos" else "Añadir a favoritos",
                        tint = if (item.esFavorito) Coral else Marino)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(item.titulo, style = MaterialTheme.typography.titleMedium, maxLines = 2, minLines = 2,
                overflow = TextOverflow.Ellipsis)
            Text(marcaDe(item), style = MaterialTheme.typography.bodyMedium, color = TextoSecundario, maxLines = 1)
            item.calificacion?.let { rating ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFBF13), modifier = Modifier.size(16.dp))
                    Text(" %.1f".format(rating), style = MaterialTheme.typography.labelMedium)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(formatearPrecio(item.precio), style = MaterialTheme.typography.titleLarge,
                color = Azul, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun ShopBottomBar(
    onInicio: () -> Unit, onComparar: () -> Unit,
    onFavoritos: () -> Unit, onCuenta: () -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 4.dp) {
        NavigationBarItem(selected = true, onClick = onInicio,
            icon = { Icon(Icons.Default.Home, "Inicio") }, label = { Text("Inicio") })
        NavigationBarItem(selected = false, onClick = onComparar,
            icon = { Icon(Icons.Default.CompareArrows, "Comparar") }, label = { Text("Comparar") })
        NavigationBarItem(selected = false, onClick = onFavoritos,
            icon = { Icon(Icons.Default.FavoriteBorder, "Favoritos") }, label = { Text("Favoritos") })
        NavigationBarItem(selected = false, onClick = onCuenta,
            icon = { Icon(Icons.Default.PersonOutline, "Mi cuenta") }, label = { Text("Mi cuenta") })
    }
}
