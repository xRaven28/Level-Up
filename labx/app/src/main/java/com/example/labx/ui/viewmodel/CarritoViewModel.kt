package com.example.labx.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.labx.data.local.AppDatabase
import com.example.labx.data.local.entity.CarritoEntity
import com.example.labx.domain.model.ItemCarrito
import com.example.labx.domain.model.Producto
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class CarritoEvento {
    data class MostrarMensaje(val mensaje: String) : CarritoEvento()
    data class NavegarAPagoExitoso(val idTransaccion: String) : CarritoEvento()
}

data class DatosOrden(
    val idTransaccion: String,
    val items: List<ItemCarrito>,
    val subtotal: Double,
    val montoDescuento: Double,
    val totalFinal: Double,
    val fecha: String,
    val cliente: String,
    val direccion: String,
    val metodoPago: String,
    val esDuoc: Boolean
)

class CarritoViewModel(application: Application) : AndroidViewModel(application) {

    // ✅ USAMOS DAO DIRECTO (SIN REPOSITORY)
    private val dao = AppDatabase.getDatabase(application).carritoDao()

    // ✅ EVENTOS
    private val _canalEventos = Channel<CarritoEvento>()
    val eventos = _canalEventos.receiveAsFlow()

    // ✅ ESTADO DE PAGO
    private val _estaProcesando = MutableStateFlow(false)
    val estaProcesando: StateFlow<Boolean> = _estaProcesando.asStateFlow()

    var ordenFinal: DatosOrden? = null
        private set

    // ✅ PRODUCTOS DISPONIBLES (SIN REPOSITORY)
    val productosDisponibles = listOf(
        Producto(1, "Mouse Gamer", "Mouse RGB 6400dpi", 25000.0, "", "Periféricos", 10),
        Producto(2, "Teclado Mecánico", "Switch Blue RGB", 45000.0, "", "Periféricos", 5),
        Producto(3, "Audífonos Gamer", "Surround 7.1", 35000.0, "", "Audio", 8)
    )

    // ✅ FLUJO REAL DEL CARRITO DESDE ROOM
    val itemsCarrito: StateFlow<List<ItemCarrito>> =
        dao.obtenerTodo()
            .map { lista ->
                lista.map { entity ->

                    val producto = Producto(
                        id = entity.productoId,
                        nombre = entity.nombre,
                        descripcion = entity.descripcion,
                        precio = entity.precio,
                        imagenUrl = entity.imagenUrl,
                        categoria = entity.categoria,
                        stock = entity.stock
                    )

                    ItemCarrito(
                        producto = producto,
                        cantidad = entity.cantidad,
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val totalCarrito: StateFlow<Double> =
        itemsCarrito.map { lista ->
            lista.sumOf { it.subtotal }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // ✅ LOGS DE DEPURACIÓN
    init {
        viewModelScope.launch {
            itemsCarrito.collect { items ->
                Log.d("CARRITO_DB", "═══════════════════════════════")
                Log.d("CARRITO_DB", "Items en carrito: ${items.size}")
                items.forEachIndexed { index, item ->
                    Log.d(
                        "CARRITO_DB",
                        "${index + 1}. ${item.producto.nombre} x${item.cantidad} - Subtotal: $${item.subtotal.toInt()}"
                    )
                }
                Log.d("CARRITO_DB", "TOTAL: $${items.sumOf { it.subtotal }}")
                Log.d("CARRITO_DB", "═══════════════════════════════")
            }
        }
    }

    fun agregarAlCarrito(producto: Producto) {
        viewModelScope.launch {

            val itemExistente = dao.obtenerPorProductoId(producto.id)

            if (itemExistente == null) {
                dao.insertar(
                    CarritoEntity(
                        productoId = producto.id,
                        nombre = producto.nombre,
                        descripcion = producto.descripcion,
                        imagenUrl = producto.imagenUrl,
                        categoria = producto.categoria,
                        stock = producto.stock,
                        precio = producto.precio,
                        cantidad = 1
                    )
                )
            } else {
                dao.actualizarCantidad(
                    productoId = producto.id,
                    cantidad = itemExistente.cantidad + 1
                )
            }

            _canalEventos.send(
                CarritoEvento.MostrarMensaje("✅ ${producto.nombre} agregado")
            )
        }
    }

    fun vaciarCarrito() {
        viewModelScope.launch {
            dao.vaciar()
            _canalEventos.send(CarritoEvento.MostrarMensaje("🗑️ Carrito vaciado"))
        }
    }

    fun modificarCantidad(productoId: Int, cantidad: Int) {
        viewModelScope.launch {
            dao.actualizarCantidad(productoId, cantidad)
        }
    }

    fun eliminarProducto(productoId: Int) {
        viewModelScope.launch {
            dao.eliminarProducto(productoId)
            _canalEventos.send(CarritoEvento.MostrarMensaje("❌ Producto eliminado"))
        }
    }

    fun procesarPago(
        nombreCliente: String,
        direccion: String,
        metodoPago: String,
        esDuoc: Boolean
    ) {
        viewModelScope.launch {

            _estaProcesando.value = true

            val itemsActuales = itemsCarrito.value
            val subtotalActual = totalCarrito.value

            Log.d("PAY", "Subtotal recibido: $subtotalActual")

            val montoDescuento = if (esDuoc) subtotalActual * 0.10 else 0.0
            val totalFinalCalculado = subtotalActual - montoDescuento

            val fechaActual =
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            val idTx = "TRX-${UUID.randomUUID().toString().take(8).uppercase()}"

            delay(2000)

            ordenFinal = DatosOrden(
                idTransaccion = idTx,
                items = itemsActuales,
                subtotal = subtotalActual,
                montoDescuento = montoDescuento,
                totalFinal = totalFinalCalculado,
                fecha = fechaActual,
                cliente = nombreCliente,
                direccion = direccion,
                metodoPago = metodoPago,
                esDuoc = esDuoc
            )

            dao.vaciar()
            _estaProcesando.value = false

            _canalEventos.send(
                CarritoEvento.NavegarAPagoExitoso(idTx)
            )
        }
    }

}
