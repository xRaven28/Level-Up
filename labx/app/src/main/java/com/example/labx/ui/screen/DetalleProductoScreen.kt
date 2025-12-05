package com.example.labx.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labx.data.repository.ProductoRepositoryImpl
import com.example.labx.domain.model.Producto
import com.example.labx.ui.viewmodel.CarritoEvento
import com.example.labx.ui.viewmodel.CarritoViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productoId: Int,
    productoRepository: ProductoRepositoryImpl,
    carritoViewModel: CarritoViewModel, // ✅ SE USA EL QUE VIENE DEL NAVGRAPH
    onVolverClick: () -> Unit
) {
    var producto by remember { mutableStateOf<Producto?>(null) }
    var estaCargando by remember { mutableStateOf(true) }

    var mostrarNotificacion by remember { mutableStateOf(false) }
    var mensajeNotificacion by remember { mutableStateOf("") }

    val context = LocalContext.current

    // ✅ Escuchar eventos del carrito
    LaunchedEffect(Unit) {
        carritoViewModel.eventos.collect { evento ->
            if (evento is CarritoEvento.MostrarMensaje) {
                mensajeNotificacion = evento.mensaje
                mostrarNotificacion = true
                delay(2000)
                mostrarNotificacion = false
            }
        }
    }

    // ✅ Cargar producto por ID
    LaunchedEffect(productoId) {
        estaCargando = true
        producto = productoRepository.obtenerProductoPorId(productoId)
        estaCargando = false
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalle del Producto") },
                    navigationIcon = {
                        IconButton(onClick = onVolverClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                when {
                    estaCargando -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    producto == null -> {
                        Text(
                            text = "Producto no encontrado",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {

                        val imageResId = context.resources.getIdentifier(
                            producto!!.imagenUrl,
                            "drawable",
                            context.packageName
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = if (imageResId != 0)
                                            imageResId
                                        else
                                            android.R.drawable.ic_menu_gallery
                                    ),
                                    contentDescription = producto!!.nombre,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Text(
                                text = producto!!.nombre,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Categoría: ${producto!!.categoria}",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Divider()

                            Text("Descripción", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            Text(producto!!.descripcion, fontSize = 16.sp)

                            Divider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Precio:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = producto!!.precioFormateado(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Stock disponible:", fontSize = 16.sp)
                                Text(
                                    text = "${producto!!.stock} unidades",
                                    fontSize = 16.sp,
                                    color = if (producto!!.hayStock)
                                        MaterialTheme.colorScheme.secondary
                                    else
                                        MaterialTheme.colorScheme.error
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    carritoViewModel.agregarAlCarrito(producto!!)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = producto!!.hayStock
                            ) {
                                Text("Agregar al Carrito")
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = mostrarNotificacion,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                shape = RoundedCornerShape(50),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = mensajeNotificacion,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
