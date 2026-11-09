package com.tecsup.productos.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.Cargando

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AdminViewModel, onVolver: () -> Unit) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    val trabajando by viewModel.trabajando.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbar.showSnackbar(it)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Admin") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        when (val e = estado) {
            is UiState.Cargando -> Cargando(modifier = Modifier.padding(padding))
            is UiState.Error -> Text(e.mensaje, Modifier.padding(padding))
            is UiState.Exito -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaTotal("Guardados", e.datos.guardados, Modifier.weight(1f))
                    TarjetaTotal("Favoritos", e.datos.favoritos, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = viewModel::sincronizar,
                    enabled = !trabajando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (trabajando) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("Sincronizar")
                }

                OutlinedButton(
                    onClick = viewModel::limpiarCache,
                    enabled = !trabajando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Limpiar caché")
                }
            }
        }
    }
}

@Composable
private fun TarjetaTotal(titulo: String, valor: Int, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor.toString(), style = MaterialTheme.typography.headlineLarge)
            Text(titulo, style = MaterialTheme.typography.bodyMedium)
        }
    }
}