package com.tecsup.productos.ui.perfil

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tecsup.productos.data.preferences.ModoTema
import com.tecsup.productos.domain.model.Rol
import com.tecsup.productos.domain.model.Sesion
import com.tecsup.productos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    sesion: Sesion,
    onVolver: () -> Unit,
    onFavoritos: () -> Unit,
    onComparar: () -> Unit,
    onAdmin: () -> Unit,
    onCerrarSesion: () -> Unit,
    modo: ModoTema = ModoTema.SISTEMA,
    onCambiarModo: (ModoTema) -> Unit = {}
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {
        TopAppBar(title = { Text("Mi cuenta", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(25.dp)).background(
                Brush.horizontalGradient(listOf(Marino, Azul, Violeta))
            ).padding(21.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(69.dp).background(Color.White.copy(alpha = .18f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Mi perfil", style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Text(sesion.correo, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                        Spacer(Modifier.height(5.dp))
                        Text(if (sesion.rol == Rol.ADMIN) "Administrador" else "Usuario",
                            color = Color(0xFFD8F4FF), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            Text("Accesos rápidos", style = MaterialTheme.typography.titleLarge)
            ProfileAction("Mis favoritos", "Tus teléfonos guardados", Icons.Default.FavoriteBorder, onFavoritos)
            ProfileAction("Comparar modelos", "Conoce las diferencias", Icons.Default.CompareArrows, onComparar)
            if (sesion.rol == Rol.ADMIN) {
                ProfileAction("Administración", "Catálogo local y sincronización", Icons.Default.Dashboard, onAdmin)
            }
            Text("Apariencia", style = MaterialTheme.typography.titleLarge)
            Card(shape = RoundedCornerShape(19.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text("Tema de la aplicación", style = MaterialTheme.typography.titleMedium)
                    Text("Elige la opción que prefieres. Se conserva al volver a abrir la app.",
                        color = TextoSecundario, style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        ModoTema.entries.forEach { opcion ->
                            FilterChip(selected = modo == opcion, onClick = { onCambiarModo(opcion) },
                                label = { Text(when (opcion) {
                                    ModoTema.SISTEMA -> "Sistema"
                                    ModoTema.CLARO -> "Claro"
                                    ModoTema.OSCURO -> "Oscuro"
                                }) })
                        }
                    }
                }
            }
            Text("Cuenta y seguridad", style = MaterialTheme.typography.titleLarge)
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(19.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Correo electrónico", color = TextoSecundario, style = MaterialTheme.typography.labelMedium)
                    Text(sesion.correo, style = MaterialTheme.typography.bodyLarge)
                    HorizontalDivider()
                    Text("Tu cuenta se autentica mediante Firebase. El rol es asignado por Firestore.",
                        color = TextoSecundario, style = MaterialTheme.typography.bodyMedium)
                }
            }
            OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(17.dp)) {
                Icon(Icons.Default.Logout, null)
                Spacer(Modifier.width(10.dp))
                Text("Cerrar sesión")
            }
        }
    }
}

@Composable
private fun ProfileAction(titulo: String, subtitulo: String, icon: ImageVector, onClick: () -> Unit) {
    Card(onClick = onClick, shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Azul, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
            }
            Icon(Icons.Default.ChevronRight, null, tint = TextoSecundario)
        }
    }
}
