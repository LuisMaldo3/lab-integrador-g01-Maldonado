package com.tecsup.productos.ui.login

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.productos.ui.components.BrandButton
import com.tecsup.productos.ui.components.HeroGradient
import com.tecsup.productos.ui.theme.Azul

@Composable
fun RecuperarPasswordExitoScreen(onVolver: () -> Unit) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().height(148.dp).background(HeroGradient)) {
            Text("SEGURIDAD DE TU CUENTA", modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleMedium, color = Color.White)
        }
        Column(Modifier.fillMaxWidth().padding(25.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(16.dp))
            Icon(Icons.Default.MarkEmailRead, "Correo de recuperación", tint = Azul,
                modifier = Modifier.size(112.dp))
            Spacer(Modifier.height(22.dp))
            Text("Revisa tu correo", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            Text("Si existe una cuenta asociada a ese correo, recibirás instrucciones para restablecer la contraseña. Revisa también spam.",
                style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))
            BrandButton("Volver a iniciar sesión", onVolver, Modifier.fillMaxWidth())
        }
    }
}
