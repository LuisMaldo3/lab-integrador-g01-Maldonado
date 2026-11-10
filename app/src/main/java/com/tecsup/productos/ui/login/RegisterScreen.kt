package com.tecsup.productos.ui.login

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.productos.R
import com.tecsup.productos.ui.components.*
import com.tecsup.productos.ui.theme.*

@Composable
fun RegisterScreen(viewModel:RegisterViewModel,onVolver:()->Unit,onCreado: (String)->Unit){
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var nombre by rememberSaveable{mutableStateOf("")}
    var correo by rememberSaveable{mutableStateOf("")}
    var password by rememberSaveable{mutableStateOf("")}
    var confirm by rememberSaveable{mutableStateOf("")}
    var acepto by rememberSaveable{mutableStateOf(false)}
    var legalDialog by remember {mutableStateOf(false)}
    val loading=estado is AuthOperation.Loading
    LaunchedEffect(estado){if(estado is AuthOperation.Success)onCreado(correo.trim())}
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).imePadding()) {
        Box(Modifier.fillMaxWidth().height(105.dp).background(HeroGradient)) {
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize()){
                val p=androidx.compose.ui.graphics.Path().apply {moveTo(0f,size.height*.96f);cubicTo(size.width*.24f,size.height*.49f,size.width*.62f,size.height*.72f,size.width,size.height*.85f);lineTo(size.width,size.height);lineTo(0f,size.height);close()}
                drawPath(p,Color(0xFFEAF4FF))
            }
            IconButton(onClick=onVolver,modifier=Modifier.align(Alignment.TopStart).statusBarsPadding().padding(start=13.dp)){
                Icon(Icons.AutoMirrored.Filled.ArrowBack,"Volver",tint=Color.White)
            }
        }
        Column(Modifier.fillMaxWidth().padding(horizontal=23.dp).padding(bottom=28.dp)) {
            Text("Crear cuenta",style=MaterialTheme.typography.headlineMedium,color=MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(3.dp))
            Text("Únete a Tienda Tecsup y descubre los mejores smartphones",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
            Image(painterResource(R.drawable.hero_registro_telefonos),"Smartphones de Tienda Tecsup",
                contentScale=ContentScale.Fit,modifier=Modifier.fillMaxWidth().height(166.dp))
            AuthField(nombre,{nombre=it},"Nombre completo",Icons.Default.Person)
            Spacer(Modifier.height(9.dp))
            AuthField(correo,{correo=it},"Correo electrónico",Icons.Default.Email,keyboardType=KeyboardType.Email)
            Spacer(Modifier.height(9.dp))
            AuthField(password,{password=it},"Contraseña",Icons.Default.Lock,password=true)
            Spacer(Modifier.height(9.dp))
            AuthField(confirm,{confirm=it},"Confirmar contraseña",Icons.Default.Lock,password=true)
            Spacer(Modifier.height(12.dp))
            Column(Modifier.fillMaxWidth().background(Suave,RoundedCornerShape(17.dp)).padding(13.dp)) {
                Text("Tu contraseña debe tener:",color=Marino,style=MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(5.dp))
                Requirement("Mínimo 8 caracteres",password.length>=8)
                Requirement("Una letra y un número",password.any(Char::isLetter)&&password.any(Char::isDigit))
                Requirement("Contraseñas coincidentes",password.isNotBlank()&&password==confirm)
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.fillMaxWidth()) {
                Checkbox(checked=acepto,onCheckedChange={acepto=it},colors=CheckboxDefaults.colors(checkedColor=Azul))
                Column(Modifier.weight(1f)) {
                    Text("Acepto los Términos y Condiciones",style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurface)
                    Text("y la Política de Privacidad · Ver documentos",color=Azul,style=MaterialTheme.typography.bodyMedium,
                        modifier=Modifier.clickable{legalDialog=true})
                }
            }
            if(estado is AuthOperation.Failure) Text((estado as AuthOperation.Failure).message,color=MaterialTheme.colorScheme.error,style=MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(9.dp))
            BrandButton("Crear mi cuenta",{viewModel.registrar(nombre,correo,password,confirm,acepto)},Modifier.fillMaxWidth(),loading=loading)
            Spacer(Modifier.height(13.dp))
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.Center,verticalAlignment=Alignment.CenterVertically) {
                Text("¿Ya tienes una cuenta? ",color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.bodyMedium)
                Text("Iniciar sesión",modifier=Modifier.clickable(onClick=onVolver),color=Azul,style=MaterialTheme.typography.labelLarge)
            }
        }
    }
    if(legalDialog) AlertDialog(onDismissRequest={legalDialog=false},title={Text("Información legal")},
        text={Text("Los documentos oficiales de Términos y Condiciones y Política de Privacidad aún deben incorporarse al proyecto. Este texto no sustituye una política legal aprobada.")},
        confirmButton={TextButton(onClick={legalDialog=false}){Text("Entendido")}})
}
@Composable private fun Requirement(text:String,ok:Boolean){
    Row(verticalAlignment=Alignment.CenterVertically) {
        Text(if(ok)"✓" else "○",color=if(ok)Color(0xFF16A34A) else Color(0xFF8391B0),style=MaterialTheme.typography.titleMedium)
        Spacer(Modifier.width(8.dp))
        Text(text,color=if(ok)Marino else TextoSecundario,style=MaterialTheme.typography.bodyMedium)
    }
}
