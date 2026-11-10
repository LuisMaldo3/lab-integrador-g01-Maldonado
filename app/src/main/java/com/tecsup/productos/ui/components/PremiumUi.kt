package com.tecsup.productos.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.productos.R
import com.tecsup.productos.ui.theme.*

val HeroGradient get() = Brush.linearGradient(listOf(AzulOscuro,Azul,Celeste))
val ButtonGradient get() = Brush.horizontalGradient(listOf(Color(0xFF0983FF),Color(0xFF003FD6)))

@Composable
fun BrandButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, loading: Boolean = false) {
    Box(modifier = modifier.height(54.dp).clip(RoundedCornerShape(18.dp))
        .background(if(enabled) ButtonGradient else Brush.horizontalGradient(listOf(Color(0xFF9CB2D7),Color(0xFF829BCA))))
        .clickable(enabled=enabled && !loading, onClick=onClick),contentAlignment=Alignment.Center) {
        if(loading) CircularProgressIndicator(Modifier.size(23.dp),color=Color.White,strokeWidth=2.dp)
        else Text(text,style=MaterialTheme.typography.titleMedium,color=Color.White)
    }
}

@Composable
fun SoftButton(text: String, onClick: () -> Unit, modifier: Modifier=Modifier, icon: ImageVector?=null) {
    Row(modifier=modifier.height(54.dp).shadow(3.dp,RoundedCornerShape(18.dp))
        .clip(RoundedCornerShape(18.dp)).background(MaterialTheme.colorScheme.surface)
        .border(1.dp,Color(0xFFCBDEFC),RoundedCornerShape(18.dp))
        .clickable(onClick=onClick),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.Center) {
        if(icon!=null) {Icon(icon,null,tint=Azul,modifier=Modifier.size(22.dp));Spacer(Modifier.width(9.dp))}
        Text(text,color=Azul,style=MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun AuthField(
    value:String,onValueChange:(String)->Unit,placeholder:String,icon:ImageVector,
    modifier:Modifier=Modifier, password:Boolean=false, keyboardType:KeyboardType=KeyboardType.Text,
    enabled:Boolean=true
) {
    var show by remember { mutableStateOf(false) }
    BasicTextField(value=value,onValueChange=onValueChange,singleLine=true,enabled=enabled,
        keyboardOptions=KeyboardOptions(keyboardType=if(password) KeyboardType.Password else keyboardType),
        visualTransformation=if(password && !show) PasswordVisualTransformation() else VisualTransformation.None,
        textStyle=MaterialTheme.typography.bodyLarge.copy(color=MaterialTheme.colorScheme.onSurface),
        cursorBrush=SolidColor(Azul),
        modifier=modifier.fillMaxWidth().height(52.dp),
        decorationBox={ inner ->
            Row(modifier=Modifier.fillMaxSize().shadow(2.dp,RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface,RoundedCornerShape(18.dp))
                .border(1.dp,MaterialTheme.colorScheme.outline,RoundedCornerShape(18.dp))
                .padding(horizontal=15.dp),verticalAlignment=Alignment.CenterVertically) {
                Icon(icon,null,tint=Azul,modifier=Modifier.size(22.dp))
                Spacer(Modifier.width(13.dp))
                Box(Modifier.weight(1f),contentAlignment=Alignment.CenterStart) {
                    if(value.isEmpty()) Text(placeholder,color=MaterialTheme.colorScheme.onSurfaceVariant,maxLines=1,overflow=TextOverflow.Ellipsis)
                    inner()
                }
                if(password) {
                    IconButton(onClick={show=!show},modifier=Modifier.size(48.dp)) {
                        Icon(if(show) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription=if(show)"Ocultar contraseña" else "Mostrar contraseña",tint=Azul)
                    }
                }
            }
        }
    )
}

@Composable
fun BrandLogo(modifier:Modifier=Modifier) {
    Image(painterResource(R.drawable.logo_tienda_tecsup_ajustado),contentDescription="Tienda Tecsup",contentScale=ContentScale.Fit,modifier=modifier)
}

@Composable
fun AuthHero(login:Boolean, modifier:Modifier=Modifier, height:Dp=310.dp) {
    Box(modifier=modifier.fillMaxWidth().height(height).background(HeroGradient)) {
        Canvas(Modifier.fillMaxSize()) {
            val p=Path().apply {moveTo(0f,size.height*.60f);cubicTo(size.width*.29f,size.height*.39f,size.width*.47f,size.height*.72f,size.width,size.height*.26f);lineTo(size.width,size.height);lineTo(0f,size.height);close()}
            drawPath(p,color=Color(0x3326DBFF))
            val wave=Path().apply {moveTo(0f,size.height*.92f);cubicTo(size.width*.28f,size.height*.79f,size.width*.61f,size.height*.78f,size.width,size.height*.91f);lineTo(size.width,size.height);lineTo(0f,size.height);close()}
            drawPath(wave,color=Color(0xFFEFF6FF))
        }
        if(login) {
            BrandLogo(Modifier.align(Alignment.TopStart).statusBarsPadding().padding(start=22.dp,top=12.dp).width(150.dp).height(60.dp))
            Image(painterResource(R.drawable.hero_login_telefonos),contentDescription="Tres smartphones de Tienda Tecsup",
                contentScale=ContentScale.Fit,modifier=Modifier.align(Alignment.BottomEnd).offset(x=21.dp,y=(-11).dp)
                    .fillMaxWidth(.96f).height(height*.84f))
        }
    }
}

@Composable
fun ColorChip(text:String, selected:Boolean,onClick:()->Unit,modifier:Modifier=Modifier) {
    Box(modifier=modifier.clip(RoundedCornerShape(50)).background(if(selected) Azul else MaterialTheme.colorScheme.surface)
        .border(1.dp,if(selected) Azul else Color(0xFFDBE7F7),RoundedCornerShape(50))
        .clickable(onClick=onClick).padding(horizontal=17.dp,vertical=10.dp),contentAlignment=Alignment.Center) {
        Text(text,color=if(selected) Color.White else MaterialTheme.colorScheme.onSurface,style=MaterialTheme.typography.labelLarge)
    }
}
