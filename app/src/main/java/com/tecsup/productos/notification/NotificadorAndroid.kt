package com.tecsup.productos.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tecsup.productos.MainActivity
import com.tecsup.productos.R

/**
 * Implementación real del Notificador con NotificationCompat.
 * Recibe Context aquí (capa de infraestructura), NUNCA en el ViewModel.
 */
class NotificadorAndroid(context: Context) : Notificador {

    private val appContext = context.applicationContext

    companion object {
        const val CANAL_ID = "sincronizacion"
        private const val NOTIFICACION_ID = 1001
    }

    init { crearCanal() }

    /** Desde Android 8 (API 26) toda notificación necesita un canal. */
    private fun crearCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Sincronización",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Avisa cuando se terminan de guardar los datos" }
            val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }

    private fun tienePermiso(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true   // antes de 13 no se pide
        return ContextCompat.checkSelfPermission(
            appContext, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun mostrar(titulo: String, texto: String) {
        // Permiso negado: la app sigue funcionando normal, simplemente no avisa (PA-14)
        if (!tienePermiso()) return

        // Al tocar la notificación se abre la app
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendiente = PendingIntent.getActivity(
            appContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(appContext, CANAL_ID)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendiente)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(appContext).notify(NOTIFICACION_ID, notificacion)
        } catch (_: SecurityException) {
        }
    }
}