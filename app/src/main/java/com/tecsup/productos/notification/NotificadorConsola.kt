package com.tecsup.productos.notification

import android.util.Log

class NotificadorConsola : Notificador {
    override fun mostrar(titulo: String, texto: String) {
        Log.d("Notificador", "$titulo: $texto")
    }
}