package com.tecsup.productos.fakes

import com.tecsup.productos.notification.Notificador

/** Guarda lo que se "mostró" para poder afirmarlo en las pruebas. */
class FakeNotificador : Notificador {
    val mostradas = mutableListOf<Pair<String, String>>()
    override fun mostrar(titulo: String, texto: String) {
        mostradas += titulo to texto
    }
}