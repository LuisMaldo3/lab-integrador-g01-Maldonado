package com.tecsup.productos.ui.components
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable fun Cargando(mensaje:String="Cargando…",modifier:Modifier=Modifier){
    Column(modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
        CircularProgressIndicator(strokeWidth=3.dp)
        Spacer(Modifier.height(17.dp))
        Text(mensaje,style=MaterialTheme.typography.bodyLarge)
    }
}
