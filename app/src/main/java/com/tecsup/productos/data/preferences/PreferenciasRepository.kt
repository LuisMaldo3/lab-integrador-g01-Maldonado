package com.tecsup.productos.data.preferences

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ModoTema { SISTEMA, CLARO, OSCURO }

interface PreferenciasRepository {
    val modoTema: StateFlow<ModoTema>
    fun guardarModoTema(modo: ModoTema)
}

class PreferenciasLocales(context: Context) : PreferenciasRepository {
    private val prefs = context.applicationContext.getSharedPreferences("tecsup_preferencias", Context.MODE_PRIVATE)
    private val _modoTema = MutableStateFlow(
        runCatching { ModoTema.valueOf(prefs.getString("modo_tema", "SISTEMA") ?: "SISTEMA") }
            .getOrDefault(ModoTema.SISTEMA)
    )
    override val modoTema: StateFlow<ModoTema> = _modoTema.asStateFlow()
    override fun guardarModoTema(modo: ModoTema) {
        prefs.edit().putString("modo_tema", modo.name).apply()
        _modoTema.value = modo
    }
}
