package com.example.labx.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.labx.domain.model.ItemCarrito
import com.example.labx.ui.viewmodel.CarritoEvento
import com.example.labx.ui.viewmodel.CarritoViewModel
import com.example.labx.ui.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.collectLatest

/* =========================
   🎨 PALETA OFICIAL NUEVA
   ========================= */

private val Onyx = Color(0xFF131515)
private val Graphite = Color(0xFF2B2C28)
private val Verdigris = Color(0xFF339989)
private val PearlAqua = Color(0xFF7DE2D1)
private val Snow = Color(0xFFFFFAFB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    carritoViewModel: CarritoViewModel,
    usuarioViewModel: UsuarioViewModel,
    onVolverClick: () -> Unit,
    onProductoClick: (Int) -> Unit,
    onIrACheckout: (String) -> Unit
) {

    val itemsCarrito by carritoViewModel.itemsCarrito.collectAsState()
    val subtotal by carritoViewModel.totalCarrito.collectAsState()
    val estaProcesando by carritoViewModel.estaProcesando.collectAsState()

    val authState by usuarioViewModel.authState.collectAsState()
    val esDuoc = authState.usuarioActual?.esDuoc == true

    val descuento = if (esDuoc) subtotal * 0.10 else 0.0
    val totalFinal = subtotal - descuento

    // ✅ ANIMACIÓN DEL TOTAL
    val totalAnimado by animateFloatAsState(
        targetValue = totalFinal.toFloat(),
        animationSpec = tween(450),
        label = "total_anim"
    )

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        carritoViewModel.eventos.collectLatest { evento ->
            when (evento) {
                is CarritoEvento.MostrarMensaje ->
                    snackbarHostState.showSnackbar(evento.mensaje)

                is CarritoEvento.NavegarAPagoExitoso -> {
                    // ✅ SOLO NAVEGACIÓN (SIN PROCESAR PAGO AQUÍ)
                    onIrACheckout(evento.idTransaccion)
                }
            }
        }
    }

    Scaffold(
        containerColor = Onyx,

        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito (${itemsCarrito.size})") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Onyx,
                    titleContentColor = Snow
                ),
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, null, tint = Snow)
                    }
                }
            )
        },

        snackbarHost = { SnackbarHost(snackbarHostState) },

        bottomBar = {
            Surface(color = Graphite) {
                Column(
                    Modifier
                        .padding(18.dp)
                        .navigationBarsPadding()
                ) {

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("TOTAL", fontSize = 13.sp, color = PearlAqua)
                            if (esDuoc && descuento > 0) {
                                Text(
                                    "Descuento DUOC aplicado",
                                    fontSize = 12.sp,
                                    color = Verdigris
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            if (esDuoc && descuento > 0) {
                                Text(
                                    formatearPrecio(subtotal),
                                    fontSize = 13.sp,
                                    color = PearlAqua,
                                    style = LocalTextStyle.current.copy(
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                )
                            }

                            Text(
                                formatearPrecio(totalAnimado.toDouble()),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Verdigris
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ✅ BOTÓN IR A CHECKOUT (YA NO PROCESA PAGO)
                    Button(
                        onClick = {
                            onIrACheckout("checkout")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = itemsCarrito.isNotEmpty() && !estaProcesando,
                        colors = ButtonDefaults.buttonColors(containerColor = Verdigris)
                    ) {
                        Text("IR A PAGAR", fontWeight = FontWeight.Bold, color = Snow)
                    }
                }
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Onyx)
        ) {

            if (itemsCarrito.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🛒", fontSize = 70.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Tu carrito está vacío", fontSize = 18.sp, color = Snow)
                    Text(
                        "Agrega productos desde el catálogo",
                        fontSize = 14.sp,
                        color = PearlAqua
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(itemsCarrito) { item ->
                        CarritoItemDarkGamer(
                            item = item,

                            onCantidadChange = { nuevaCantidad ->
                                carritoViewModel.modificarCantidad(
                                    productoId = item.producto.id,
                                    cantidad = nuevaCantidad
                                )
                            },

                            onEliminarClick = {
                                carritoViewModel.eliminarProducto(item.producto.id)
                            },

                            onClick = {
                                onProductoClick(item.producto.id)
                            }
                        )
                    }
                }
            }
        }
    }
}               /* =========================
                  💲 FORMATO PRECIO
                   ========================= */

fun formatearPrecio(precio: Double): String {
    val precioEntero = precio.toInt()
    return "$" + precioEntero
        .toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}
@Composable
fun CarritoItemDarkGamer(
    item: ItemCarrito,
    onCantidadChange: (Int) -> Unit,
    onEliminarClick: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val imageModel = remember(item.producto.imagenUrl) {
        if (item.producto.imagenUrl.startsWith("http")) {
            item.producto.imagenUrl
        } else {
            val resId = context.resources.getIdentifier(
                item.producto.imagenUrl,
                "drawable",
                context.packageName
            )
            if (resId != 0) resId else android.R.drawable.ic_menu_gallery
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Graphite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = imageModel,
                contentDescription = item.producto.nombre,
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Onyx),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(item.producto.nombre, fontWeight = FontWeight.Bold, color = Snow)

                Text(
                    formatearPrecio(item.producto.precio),
                    fontSize = 14.sp,
                    color = PearlAqua
                )

                Row(verticalAlignment = Alignment.CenterVertically) {

                    IconButton(
                        enabled = item.cantidad > 1,
                        onClick = { onCantidadChange(item.cantidad - 1) }
                    ) {
                        Text("−", color = Snow, fontSize = 22.sp)
                    }

                    Text(
                        "${item.cantidad}",
                        color = Snow,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = { onCantidadChange(item.cantidad + 1) }) {
                        Icon(Icons.Default.Add, null, tint = Snow)
                    }
                }

                Text(
                    "Subtotal ${formatearPrecio(item.subtotal)}",
                    fontWeight = FontWeight.Bold,
                    color = Verdigris
                )
            }

            IconButton(onClick = onEliminarClick) {
                Icon(Icons.Default.Delete, null, tint = Verdigris)
            }
        }
    }
}

