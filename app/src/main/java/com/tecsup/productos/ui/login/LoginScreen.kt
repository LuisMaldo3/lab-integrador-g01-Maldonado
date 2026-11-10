package com.tecsup.productos.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.productos.ui.UiState
import com.tecsup.productos.ui.components.*
import com.tecsup.productos.ui.theme.*

@Composable fun LoginScreen(viewModel:LoginViewModel) = LoginScreen(viewModel, {}, {}, "")

@Composable
fun LoginScreen(viewModel: LoginViewModel, onCrearCuenta:()->Unit,onRecuperar:()->Unit,correoPrecargado:String){
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var correo by rememberSaveable { mutableStateOf(correoPrecargado) }
    var clave by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(correoPrecargado){if(correoPrecargado.isNotBlank())correo=correoPrecargado}
    val cargando=estado is UiState.Cargando
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).verticalScroll(rememberScrollState()).imePadding()) {
        AuthHero(login=true,height=315.dp)
        Column(Modifier.fillMaxWidth().padding(horizontal=23.dp).padding(top=10.dp,bottom=28.dp)) {
            Text("Bienvenido de nuevo",style=MaterialTheme.typography.headlineMedium,color=MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(5.dp))
            Text("Tu próximo smartphone está aquí",style=MaterialTheme.typography.bodyLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            AuthField(correo,{correo=it},"Correo electrónico",Icons.Default.Email,
                keyboardType=KeyboardType.Email,enabled=!cargando)
            Spacer(Modifier.height(13.dp))
            AuthField(clave,{clave=it},"Contraseña",Icons.Default.Lock,password=true,enabled=!cargando)
            Spacer(Modifier.height(7.dp))
            TextButton(onClick=onRecuperar,modifier=Modifier.align(Alignment.End)) {
                Text("¿Olvidaste tu contraseña?",color=Azul,style=MaterialTheme.typography.labelLarge)
            }
            val error=(estado as? UiState.Error)?.mensaje
            if(error!=null){Text(error,color=MaterialTheme.colorScheme.error,style=MaterialTheme.typography.bodyMedium)}
            Spacer(Modifier.height(11.dp))
            BrandButton("Iniciar sesión",{viewModel.iniciarSesion(correo,clave)},Modifier.fillMaxWidth(),loading=cargando)
            Row(Modifier.fillMaxWidth().padding(vertical=13.dp),verticalAlignment=Alignment.CenterVertically) {
                HorizontalDivider(Modifier.weight(1f),color=MaterialTheme.colorScheme.outline)
                Text("  o  ",color=MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalDivider(Modifier.weight(1f),color=MaterialTheme.colorScheme.outline)
            }
            SoftButton("Crear cuenta",onCrearCuenta,Modifier.fillMaxWidth(),Icons.Default.PersonAdd)
        }
    }
}
