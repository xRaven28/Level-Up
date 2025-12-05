package com.example.labx.utils

import android.graphics.Bitmap
import android.net.Uri
import com.example.labx.data.local.PreferenciasManager
import java.io.File
import java.io.FileOutputStream

fun guardarBitmapLocal(bitmap: Bitmap, prefs: PreferenciasManager): String {

    val context = prefs.context
    val file = File(context.filesDir, "foto_perfil.png")

    // Guardar el bitmap en un archivo
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    // Convertir archivo a URI
    val uri = Uri.fromFile(file).toString()

    // Guardar en preferencias
    prefs.guardarFotoPerfil(uri)

    return uri
}
