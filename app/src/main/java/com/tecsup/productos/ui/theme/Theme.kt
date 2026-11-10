package com.tecsup.productos.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
private val Light = lightColorScheme(
    primary=Azul, onPrimary=Nieve, primaryContainer=Suave, onPrimaryContainer=Marino,
    secondary=Royal, onSecondary=Nieve, secondaryContainer=Suave,
    tertiary=Violeta, background=Nieve, onBackground=Grafito,
    surface=androidx.compose.ui.graphics.Color.White, onSurface=Grafito,
    surfaceVariant=Suave, onSurfaceVariant=TextoSecundario,
    outline=androidx.compose.ui.graphics.Color(0xFFDCE5F3)
)
private val Dark = darkColorScheme(
    primary=AzulClaro, onPrimary=Marino, primaryContainer=AzulOscuro,
    secondary=Celeste, tertiary=Violeta, background=androidx.compose.ui.graphics.Color(0xFF0C1430),
    onBackground=Nieve, surface=androidx.compose.ui.graphics.Color(0xFF172544),onSurface=Nieve,
    surfaceVariant=androidx.compose.ui.graphics.Color(0xFF23335C),
    onSurfaceVariant=androidx.compose.ui.graphics.Color(0xFFBFC9DE)
)
@Composable
fun ProductosTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme=if(darkTheme) Dark else Light,
        typography=Typography,
        shapes=Shapes(small=RoundedCornerShape(12.dp),medium=RoundedCornerShape(18.dp),large=RoundedCornerShape(24.dp)),
        content=content
    )
}
