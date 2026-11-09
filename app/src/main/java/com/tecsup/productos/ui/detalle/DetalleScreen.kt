package com.tecsup.productos.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tecsup.productos.data.mapper.formatearPrecio
import com.tecsup.productos.domain.model.Item
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.Cargando
import com.tecsup.productos.ui.components.ErrorView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel,
    onVolver: () -> Unit
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        when (val e = estado) {
            is UiState.Cargando -> Cargando(modifier = Modifier.padding(padding))
            is UiState.Error -> ErrorView(e.mensaje, viewModel::cargar, Modifier.padding(padding))
            is UiState.Exito -> ContenidoDetalle(e.datos, Modifier.padding(padding))
        }
    }
}

@Composable
private fun ContenidoDetalle(item: Item, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = item.imagenes.firstOrNull() ?: item.imagenUrl,
            contentDescription = item.titulo,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(item.titulo, style = MaterialTheme.typography.headlineSmall)
        Text(
            listOfNotNull(item.marca, item.categoria).joinToString(" · "),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            formatearPrecio(item.precio),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Fila("Calificación", item.calificacion?.let { "$it / 5" } ?: "-")
                Fila("Stock", item.stock?.toString() ?: "-")
                Fila("Descuento", item.descuento?.let { "$it %" } ?: "-")
                HorizontalDivider(Modifier.padding(vertical = 6.dp))
                Text("Descripción", style = MaterialTheme.typography.titleMedium)
                Text(item.descripcion ?: "-", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun Fila(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, style = MaterialTheme.typography.bodyLarge)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}