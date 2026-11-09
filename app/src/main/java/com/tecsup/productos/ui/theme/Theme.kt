package com.tecsup.productos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = Azul,
    onPrimary = Nieve,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,
    secondary = Grafito,
    tertiary = Ambar,
    background = Nieve,
    surface = Nieve
)

private val EsquemaOscuro = darkColorScheme(
    primary = AzulClaro,
    onPrimary = Grafito,
    primaryContainer = AzulOscuro,
    onPrimaryContainer = AzulClaro,
    secondary = AzulClaro,
    tertiary = Ambar
)

@Composable
fun ProductosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
