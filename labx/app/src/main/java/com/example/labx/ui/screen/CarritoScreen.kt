package com.example.labx.ui.screen

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
import coil.request.ImageRequest
import com.example.labx.domain.model.ItemCarrito
import com.example.labx.ui.viewmodel.CarritoEvento
import com.example.labx.ui.viewmodel.CarritoViewModel
import com.example.labx.ui.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.collectLatest

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

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        carritoViewModel.eventos.collectLatest { evento ->
            when (evento) {
                is CarritoEvento.MostrarMensaje -> snackbarHostState.showSnackbar(evento.mensaje)
                is CarritoEvento.NavegarAPagoExitoso -> onIrACheckout(evento.idTransaccion)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito (${itemsCarrito.size})") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (itemsCarrito.isNotEmpty()) {
                        IconButton(onClick = { carritoViewModel.vaciarCarrito() }) {
                            Icon(Icons.Default.Delete, "Vaciar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },

        // ✅ TOTAL SIEMPRE VISIBLE
        bottomBar = {
            Surface(
                shadowElevation = 16.dp,
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TOTAL:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            if (esDuoc && descuento > 0) {
                                Text(
                                    "Descuento DUOC aplicado",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            if (esDuoc && descuento > 0) {
                                Text(
                                    formatearPrecio(subtotal),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = LocalTextStyle.current.copy(
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                )
                            }

                            Text(
                                formatearPrecio(totalFinal),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            carritoViewModel.procesarPago(
                                nombreCliente = authState.usuarioActual?.nombreCompleto ?: "Cliente",
                                direccion = authState.usuarioActual?.direccion ?: "Sin dirección",
                                metodoPago = "Tarjeta",
                                esDuoc = esDuoc
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = itemsCarrito.isNotEmpty() && !estaProcesando
                    ) {
                        if (estaProcesando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("PAGAR AHORA", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (itemsCarrito.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🛒", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Tu carrito está vacío", fontSize = 18.sp)
                    Button(onClick = onVolverClick, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Ir a comprar")
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(itemsCarrito) { item ->
                        CarritoItemCard(
                            item = item,
                            onCantidadChange = {
                                carritoViewModel.modificarCantidad(item.producto.id, it)
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
}

// ---------------- ITEM -------------------

@Composable
fun CarritoItemCard(
    item: ItemCarrito,
    onCantidadChange: (Int) -> Unit,
    onEliminarClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val context = LocalContext.current
            val imageResId = context.resources.getIdentifier(
                item.producto.imagenUrl,
                "drawable",
                context.packageName
            )

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(if (imageResId != 0) imageResId else android.R.drawable.ic_menu_gallery)
                    .crossfade(true)
                    .build(),
                contentDescription = item.producto.nombre,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(item.producto.nombre, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

                Text(
                    "Precio: ${formatearPrecio(item.producto.precio)}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        enabled = item.cantidad > 1,
                        onClick = { onCantidadChange(item.cantidad - 1) }
                    ) {
                        Icon(Icons.Default.Delete, null)
                    }

                    Text("${item.cantidad}", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    IconButton(onClick = { onCantidadChange(item.cantidad + 1) }) {
                        Icon(Icons.Default.Add, null)
                    }
                }

                Text(
                    "Subtotal: ${formatearPrecio(item.subtotal)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onEliminarClick) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

fun formatearPrecio(precio: Double): String {
    val precioEntero = precio.toInt()
    return "$${precioEntero.toString().reversed().chunked(3).joinToString(".").reversed()}"
}
