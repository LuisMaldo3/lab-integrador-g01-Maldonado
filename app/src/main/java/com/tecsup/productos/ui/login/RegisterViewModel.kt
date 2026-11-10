package com.tecsup.productos.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productos.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthOperation {
    data object Idle: AuthOperation
    data object Loading: AuthOperation
    data object Success: AuthOperation
    data class Failure(val message:String): AuthOperation
}

class RegisterViewModel(private val repo:AuthRepository):ViewModel() {
    private val _estado=MutableStateFlow<AuthOperation>(AuthOperation.Idle)
    val estado:StateFlow<AuthOperation> = _estado.asStateFlow()
    fun registrar(nombre:String,correo:String,clave:String,confirmacion:String,acepto:Boolean){
        if(_estado.value is AuthOperation.Loading)return
        val error=when {
            nombre.trim().length<2 -> "Ingresa tu nombre completo"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> "Ingresa un correo válido"
            clave.length<8 || !clave.any(Char::isLetter) || !clave.any(Char::isDigit) -> "Usa al menos 8 caracteres, una letra y un número"
            clave!=confirmacion -> "Las contraseñas no coinciden"
            !acepto -> "Debes aceptar las condiciones antes de registrarte"
            else -> null
        }
        if(error!=null) { _estado.value=AuthOperation.Failure(error);return }
        viewModelScope.launch {
            _estado.value=AuthOperation.Loading
            repo.registrar(nombre,correo,clave).fold(
                onSuccess={_estado.value=AuthOperation.Success},
                onFailure={_estado.value=AuthOperation.Failure(it.message ?: "No se pudo crear la cuenta")}
            )
        }
    }
}
