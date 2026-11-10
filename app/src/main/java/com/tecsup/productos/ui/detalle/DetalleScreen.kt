package com.tecsup.productos.ui.detalle

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tecsup.productos.data.mapper.formatearPrecio
import com.tecsup.productos.domain.model.Item
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.Cargando
import com.tecsup.productos.ui.components.ErrorView
import com.tecsup.productos.ui.lista.ItemCard
import com.tecsup.productos.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel,
    onVolver: () -> Unit,
    onComparar: (Int) -> Unit = {},
    onRelacionado: (Int) -> Unit = {}
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val catalogo by viewModel.catalogo.collectAsStateWithLifecycle()
    val favorito by viewModel.favorito.collectAsStateWithLifecycle()
    val sinInternet by viewModel.sinInternet.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val producto = (estado as? UiState.Exito)?.datos
    Scaffold(containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(title = { Text("Detalle del teléfono", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = { IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                } }, actions = {
                    if (producto != null) {
                        IconButton(onClick = viewModel::alternarFavorito) {
                            Icon(if (favorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                if (favorito) "Quitar favorito" else "Guardar favorito", tint = if (favorito) Coral else Azul)
                        }
                        IconButton(onClick = {
                            val txt = "Mira este smartphone: ${producto.titulo} · ${formatearPrecio(producto.precio)}"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"; putExtra(Intent.EXTRA_TEXT, txt)
                            }
                            context.startActivity(Intent.createChooser(intent, "Compartir teléfono"))
                        }) { Icon(Icons.Default.Share, "Compartir") }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
        }
    ) { pad ->
        when (val e = estado) {
            is UiState.Cargando -> Cargando(modifier = Modifier.padding(pad))
            is UiState.Error -> ErrorView(e.mensaje, viewModel::cargar, Modifier.padding(pad))
            is UiState.Exito -> ContenidoDetalle(e.datos, catalogo, favorito, sinInternet,
                onFavorito = viewModel::alternarFavorito,
                onComparar = { onComparar(e.datos.id) }, onRelacionado = onRelacionado,
                modifier = Modifier.padding(pad))
        }
    }
}

@Composable
private fun ContenidoDetalle(
    item: Item, catalogo: List<Item>, favorito: Boolean, sinInternet: Boolean,
    onFavorito: () -> Unit, onComparar: () -> Unit,
    onRelacionado: (Int) -> Unit, modifier: Modifier = Modifier
) {
    val fotos = remember(item) { (item.imagenes + listOfNotNull(item.imagenUrl)).filter { it.isNotBlank() }.distinct() }
    val paginas = maxOf(fotos.size, 1)
    val pager = rememberPagerState(pageCount = { paginas })
    val scope = rememberCoroutineScope()
    var expandirDescripcion by remember(item.id) { mutableStateOf(false) }
    var fotoAmpliada by remember { mutableStateOf<Int?>(null) }
    val marca = item.marca?.takeIf { it.isNotBlank() } ?: item.subtitulo.substringBefore(" · ")
    val relacionados = remember(catalogo, item) {
        catalogo.filter { it.id != item.id }.sortedWith(
            compareByDescending<Item> { (it.marca ?: it.subtitulo.substringBefore(" · ")).equals(marca, true) }
                .thenBy { kotlin.math.abs(it.precio - item.precio) }
        ).take(8)
    }
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        if (sinInternet) {
            Row(Modifier.fillMaxWidth().background(Suave, RoundedCornerShape(13.dp)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WifiOff, null, tint = Azul)
                Spacer(Modifier.width(8.dp))
                Text("Sin conexión. Mostrando los datos básicos guardados.", color = Marino,
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.fillMaxWidth().padding(11.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalPager(state = pager, modifier = Modifier.fillMaxWidth().height(323.dp)) { index ->
                    Box(Modifier.fillMaxSize().clickable(enabled = fotos.isNotEmpty()) { fotoAmpliada = index },
                        contentAlignment = Alignment.Center) {
                        if (fotos.isEmpty()) {
                            Icon(Icons.Default.Smartphone, "Fotografía no disponible", tint = Azul,
                                modifier = Modifier.size(80.dp))
                        } else AsyncImage(model = fotos[index],
                            contentDescription = "Ampliar imagen ${index + 1} de ${fotos.size}",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize().padding(12.dp))
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    repeat(paginas) { index ->
                        Box(Modifier.padding(horizontal = 3.dp)
                            .width(if (index == pager.currentPage) 21.dp else 7.dp).height(7.dp)
                            .clip(CircleShape).background(if (index == pager.currentPage) Azul else Color(0xFFD3DBEC)))
                    }
                }
                if (fotos.isNotEmpty()) {
                    Text("Toca la imagen para ampliarla", style = MaterialTheme.typography.labelMedium,
                        color = TextoSecundario, modifier = Modifier.padding(top = 9.dp, bottom = 4.dp))
                }
                if (fotos.size > 1) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(9.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(fotos.size) { i ->
                            Box(Modifier.size(64.dp).clip(RoundedCornerShape(12.dp))
                                .border(if (pager.currentPage == i) 2.dp else 1.dp,
                                    if (pager.currentPage == i) Azul else Color(0xFFDDE6F6), RoundedCornerShape(12.dp))
                                .clickable { scope.launch { pager.animateScrollToPage(i) } },
                                contentAlignment = Alignment.Center) {
                                AsyncImage(fotos[i], "Ver imagen ${i + 1} de ${fotos.size}",
                                    Modifier.fillMaxSize().padding(4.dp), contentScale = ContentScale.Fit)
                            }
                        }
                    }
                }
            }
        }
        Card(shape = RoundedCornerShape(23.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(19.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.background(Suave, RoundedCornerShape(13.dp)).padding(horizontal = 11.dp, vertical = 7.dp)) {
                        Text(marca.uppercase(), style = MaterialTheme.typography.labelMedium, color = Azul)
                    }
                    Spacer(Modifier.weight(1f))
                    item.calificacion?.let {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFF3B500), modifier = Modifier.size(20.dp))
                        Text(" %.1f / 5".format(it), style = MaterialTheme.typography.labelLarge)
                    }
                }
                Text(item.titulo, fontSize = 25.sp, lineHeight = 31.sp, fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface)
                item.categoria?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario) }
                Text(formatearPrecio(item.precio), fontSize = 29.sp, fontWeight = FontWeight.ExtraBold, color = Azul)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.descuento?.takeIf { it > 0 }?.let {
                        Text("-${it.toInt()}%", color = Color.White, style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.background(Coral, RoundedCornerShape(10.dp)).padding(horizontal = 10.dp, vertical = 6.dp))
                    }
                    item.stock?.let { stock ->
                        Text(if (stock > 0) "En stock: $stock" else "Agotado", style = MaterialTheme.typography.labelLarge,
                            color = if (stock > 0) Color(0xFF18753B) else Color(0xFFB02030),
                            modifier = Modifier.background(if (stock > 0) Color(0xFFE2F8EB) else Color(0xFFFDE8EC), RoundedCornerShape(11.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp))
                    }
                }
                HorizontalDivider()
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onFavorito, modifier = Modifier.weight(1f).height(51.dp),
                        shape = RoundedCornerShape(14.dp)) {
                        Icon(if (favorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null,
                            tint = if (favorito) Coral else Azul)
                        Spacer(Modifier.width(5.dp))
                        Text(if (favorito) "Guardado" else "Favorito", maxLines = 1)
                    }
                    Button(onClick = onComparar, modifier = Modifier.weight(1f).height(51.dp),
                        shape = RoundedCornerShape(14.dp)) {
                        Icon(Icons.Default.CompareArrows, null)
                        Spacer(Modifier.width(5.dp))
                        Text("Comparar", maxLines = 1)
                    }
                }
            }
        }
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Características", style = MaterialTheme.typography.titleLarge)
                SpecRow("Marca", marca)
                item.categoria?.let { SpecRow("Categoría", it) }
                item.calificacion?.let { SpecRow("Calificación", "%.1f / 5".format(it)) }
                item.stock?.let { SpecRow("Disponibilidad", if (it > 0) "$it unidades" else "Agotado") }
                item.descuento?.takeIf { it > 0 }?.let { SpecRow("Descuento", "${it.toInt()}%") }
                HorizontalDivider()
                Text("Descripción del producto", style = MaterialTheme.typography.titleMedium)
                val descripcion = item.descripcion?.takeIf { it.isNotBlank() } ?: "No hay descripción disponible para este modelo."
                Text(descripcion, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (expandirDescripcion) Int.MAX_VALUE else 4,
                    overflow = TextOverflow.Ellipsis)
                if (descripcion.length > 165) {
                    TextButton(onClick = { expandirDescripcion = !expandirDescripcion }) {
                        Text(if (expandirDescripcion) "Leer menos" else "Leer descripción completa")
                    }
                }
            }
        }
        if (relacionados.isNotEmpty()) {
            Text("También te pueden interesar", style = MaterialTheme.typography.titleLarge)
            Text("Más smartphones de nuestro catálogo", color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 10.dp)) {
                items(relacionados, key = { it.id }) { p ->
                    Box(Modifier.width(184.dp)) {
                        ItemCard(p, onClick = { onRelacionado(p.id) }, onFavorito = {}, mostrarFavorito = false)
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
    }
    fotoAmpliada?.let { indice ->
        if (fotos.isNotEmpty()) FullscreenPhoto(fotos, indice, onDismiss = { fotoAmpliada = null })
    }
}

@Composable
private fun SpecRow(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, color = TextoSecundario, style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f))
        Text(valor, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f),
            maxLines = 3, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun FullscreenPhoto(fotos: List<String>, inicial: Int, onDismiss: () -> Unit) {
    var indice by remember { mutableIntStateOf(inicial) }
    var zoom by remember(indice) { mutableFloatStateOf(1f) }
    var offsetX by remember(indice) { mutableFloatStateOf(0f) }
    var offsetY by remember(indice) { mutableFloatStateOf(0f) }
    val state = rememberTransformableState { factor, pan, _ ->
        zoom = (zoom * factor).coerceIn(1f, 4f)
        offsetX = if (zoom > 1f) (offsetX + pan.x).coerceIn(-650f, 650f) else 0f
        offsetY = if (zoom > 1f) (offsetY + pan.y).coerceIn(-800f, 800f) else 0f
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(Color(0xFF080E22))) {
            AsyncImage(model = fotos[indice], contentDescription = "Imagen ampliada ${indice + 1}",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().padding(17.dp).graphicsLayer {
                    scaleX = zoom; scaleY = zoom; translationX = offsetX; translationY = offsetY
                }.transformable(state).pointerInput(indice) {
                    detectTapGestures(onDoubleTap = { zoom = if (zoom > 1f) 1f else 2f })
                })
            Row(Modifier.fillMaxWidth().align(Alignment.TopCenter).statusBarsPadding().padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${indice + 1} / ${fotos.size}", color = Color.White)
                IconButton(onClick = onDismiss, modifier = Modifier.background(Color.White.copy(alpha = .13f), CircleShape)) {
                    Icon(Icons.Default.Close, "Cerrar imagen", tint = Color.White)
                }
            }
            Row(Modifier.fillMaxWidth().align(Alignment.BottomCenter).navigationBarsPadding().padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = { if (indice > 0) indice-- }, enabled = indice > 0) {
                    Icon(Icons.Default.ChevronLeft, "Imagen anterior", tint = if (indice > 0) Color.White else Color.Gray)
                }
                Text("Pellizca para ampliar", color = Color.White,
                    style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.CenterVertically))
                IconButton(onClick = { if (indice < fotos.lastIndex) indice++ }, enabled = indice < fotos.lastIndex) {
                    Icon(Icons.Default.ChevronRight, "Siguiente imagen", tint = if (indice < fotos.lastIndex) Color.White else Color.Gray)
                }
            }
        }
    }
}
