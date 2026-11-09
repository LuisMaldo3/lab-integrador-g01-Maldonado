package com.tecsup.productos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tecsup.productos.ui.lista.DatosPrueba
import com.tecsup.productos.ui.lista.ListaScreen
import com.tecsup.productos.ui.theme.ProductosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProductosTheme {
                ListaScreen(items = DatosPrueba.items)
            }
        }
    }
}