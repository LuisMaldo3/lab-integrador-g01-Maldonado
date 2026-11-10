package com.tecsup.productos.ui.login

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.productos.ui.components.*
import com.tecsup.productos.ui.theme.*

@Composable fun RecuperarPasswordScreen(viewModel:RecuperarPasswordViewModel,onVolver:()->Unit,onEnviado:()->Unit){
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var correo by rememberSaveable{mutableStateOf("")}
    LaunchedEffect(estado){if(estado is AuthOperation.Success)onEnviado()}
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).imePadding()) {
        Box(Modifier.fillMaxWidth().height(190.dp).background(HeroGradient)) {
            IconButton(onClick=onVolver,modifier=Modifier.statusBarsPadding().padding(start=12.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack,"Volver",tint=Color.White)
            }
            Icon(Icons.Default.LockReset,null,modifier=Modifier.size(95.dp).align(Alignment.Center),tint=Color.White)
        }
        Column(Modifier.fillMaxWidth().padding(24.dp)) {
            Text("Recuperar contraseña",style=MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(9.dp))
            Text("Escribe tu correo electrónico para solicitar un enlace de recuperación.",style=MaterialTheme.typography.bodyLarge,color=TextoSecundario)
            Spacer(Modifier.height(27.dp))
            AuthField(correo,{correo=it},"Correo electrónico",Icons.Default.Email,keyboardType=KeyboardType.Email)
            if(estado is AuthOperation.Failure){
                Spacer(Modifier.height(10.dp))
                Text((estado as AuthOperation.Failure).message,color=MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(22.dp))
            BrandButton("Enviar enlace de recuperación",{viewModel.solicitar(correo)},Modifier.fillMaxWidth(),loading=estado is AuthOperation.Loading)
            Spacer(Modifier.height(14.dp))
            SoftButton("Volver a iniciar sesión",onVolver,Modifier.fillMaxWidth())
        }
    }
}
