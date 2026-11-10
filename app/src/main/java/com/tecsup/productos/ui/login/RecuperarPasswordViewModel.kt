package com.tecsup.productos.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.productos.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecuperarPasswordViewModel(private val repo:AuthRepository):ViewModel(){
    private val _estado=MutableStateFlow<AuthOperation>(AuthOperation.Idle)
    val estado:StateFlow<AuthOperation> = _estado.asStateFlow()
    fun solicitar(correo:String){
        if(_estado.value is AuthOperation.Loading)return
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()){
            _estado.value=AuthOperation.Failure("Ingresa un correo válido")
            return
        }
        viewModelScope.launch {
            _estado.value=AuthOperation.Loading
            repo.recuperarPassword(correo).fold(
                onSuccess={_estado.value=AuthOperation.Success},
                onFailure={_estado.value=AuthOperation.Failure(it.message ?: "No se pudo enviar la solicitud")}
            )
        }
    }
}
