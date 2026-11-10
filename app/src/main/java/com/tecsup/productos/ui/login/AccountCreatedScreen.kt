package com.tecsup.productos.ui.login

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tecsup.productos.ui.components.BrandButton
import com.tecsup.productos.ui.components.HeroGradient
import com.tecsup.productos.ui.theme.*

@Composable
fun AccountCreatedScreen(correo: String, onIrALogin: () -> Unit) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().height(146.dp).background(HeroGradient)) {
            Text("TIENDA TECSUP", modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleLarge, color = Color.White)
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CheckCircle, "Cuenta creada correctamente",
                modifier = Modifier.size(120.dp), tint = Color(0xFF13B85D))
            Spacer(Modifier.height(23.dp))
            Text("¡Cuenta creada!", style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text("Tu registro se completó. Ya puedes entrar con tu correo y contraseña.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(28.dp))
            Card(shape = RoundedCornerShape(21.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, null, tint = Azul)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Correo registrado", color = TextoSecundario,
                            style = MaterialTheme.typography.bodyMedium)
                        Text(correo, style = MaterialTheme.typography.titleMedium,
                            maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
            BrandButton("Ir a iniciar sesión", onIrALogin, Modifier.fillMaxWidth())
        }
    }
}
