// data/local/SesionUsuarioManager.kt
package com.example.labx.data.local

import android.content.Context

class SesionUsuarioManager(context: Context) {

    private val prefs = context.getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE)

    fun guardarUsuarioActivo(idUsuario: Long) {
        prefs.edit().putLong("usuario_id", idUsuario).apply()
    }

    fun obtenerUsuarioActivoId(): Long? {
        val id = prefs.getLong("usuario_id", -1L)
        return if (id == -1L) null else id
    }

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}
