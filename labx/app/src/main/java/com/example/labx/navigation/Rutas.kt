package com.example.labx.ui.navigation

object Rutas {
    const val PORTADA = "portada"
    const val HOME = "home"
    const val DETALLE = "detalle"
    const val CARRITO = "carrito"
    const val MI_CUENTA = "mi_cuenta"
    const val LOGIN_USUARIO = "login_usuario"
    const val REGISTRO_USUARIO = "registro_usuario"

    const val CHECKOUT = "checkout"
    const val PAGO_EXITOSO = "pago_exitoso"
    const val LOGIN_ADMIN = "login_admin"
    const val PANEL_ADMIN = "panel_admin"
    const val PRODUCTOS_API = "productos_api"

    const val FORMULARIO_PRODUCTO = "formulario_producto?productoId={productoId}"

    fun detalleConId(id: Int): String {
        return "$DETALLE/$id"
    }
    fun formularioEditar(id: Int): String {
        return "formulario_producto?productoId=$id"
    }
    fun pagoExitosoConId(idTransaccion: String): String {
        return "$PAGO_EXITOSO/$idTransaccion"
    }
}
