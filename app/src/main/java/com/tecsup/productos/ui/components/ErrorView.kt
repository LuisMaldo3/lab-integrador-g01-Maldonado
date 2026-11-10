package com.tecsup.productos.ui.components
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.productos.ui.theme.Azul
@Composable fun ErrorView(mensaje:String,onReintentar:()->Unit,modifier:Modifier=Modifier,textoBoton:String="Reintentar"){
    Column(modifier.fillMaxSize().padding(22.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
        Icon(Icons.Default.WarningAmber,null,tint=Azul,modifier=Modifier.size(60.dp))
        Spacer(Modifier.height(15.dp))
        Text(mensaje,style=MaterialTheme.typography.bodyLarge,textAlign=TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        BrandButton(textoBoton,onReintentar,Modifier.fillMaxWidth())
    }
}
