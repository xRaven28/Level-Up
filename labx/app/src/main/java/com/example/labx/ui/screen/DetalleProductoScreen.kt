package com.example.labx.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.labx.domain.model.Producto
import com.example.labx.domain.repository.RepositorioProductos
import com.example.labx.ui.viewmodel.CarritoEvento
import com.example.labx.ui.viewmodel.CarritoViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productoId: Int,
    productoRepository: RepositorioProductos,
    carritoViewModel: CarritoViewModel,
    onVolverClick: () -> Unit
) {
    var producto by remember { mutableStateOf<Producto?>(null) }
    var estaCargando by remember { mutableStateOf(true) }

    var mostrarNotificacion by remember { mutableStateOf(false) }
    var mensajeNotificacion by remember { mutableStateOf("") }

    val context = LocalContext.current

    // ✅ ANIMACIÓN SUAVE DEL PRECIO
    val animacionPrecio = rememberInfiniteTransition(label = "precio_anim")
    val escalaPrecio by animacionPrecio.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala"
    )

    // ✅ EVENTOS DEL CARRITO
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

    LaunchedEffect(productoId) {
        estaCargando = true
        producto = productoRepository.obtenerProductoPorId(productoId)
        estaCargando = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        // ✅ FONDO CLARO (IGUAL QUE EL HOME)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFF2F3F7),
                            Color.White
                        )
                    )
                )
                .padding(padding)
        ) {

            when {
                estaCargando -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                producto == null -> {
                    Text("Producto no encontrado", color = Color.DarkGray)
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
                    ) {

                        // ✅ HERO CON LILA → MORADO
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFFD1BFFF),
                                            Color(0xFF7B4AE2)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model =
                                    if (producto!!.imagenUrl.startsWith("http"))
                                        producto!!.imagenUrl
                                    else if (imageResId != 0)
                                        imageResId
                                    else android.R.drawable.ic_menu_gallery,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(22.dp)
                                    .clip(RoundedCornerShape(26.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }

                        // ✅ PRECIO FLOTANTE OSCURO
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-26).dp)
                                .zIndex(10f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 24.dp)
                                    .graphicsLayer {
                                        scaleX = escalaPrecio
                                        scaleY = escalaPrecio
                                    }
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF6A3FC9),
                                                Color(0xFF4C2A85)
                                            )
                                        )
                                    )
                                    .shadow(18.dp, RoundedCornerShape(22.dp))
                                    .padding(horizontal = 26.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    producto!!.precioFormateado(),
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                            }
                        }

                        // ✅ CARD LILA CLARA
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-12).dp),
                            shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE6D9FF)
                            ),
                            elevation = CardDefaults.cardElevation(14.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {

                                Text(
                                    producto!!.nombre,
                                    color = Color(0xFF2B1454),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    producto!!.categoria,
                                    color = Color(0xFF4C2A85),
                                    fontSize = 13.sp
                                )

                                Divider(color = Color(0xFFB39DDB))

                                Text(
                                    "Descripción",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2B1454)
                                )

                                Text(
                                    producto!!.descripcion,
                                    color = Color(0xFF4C2A85),
                                    fontSize = 14.sp
                                )

                                Spacer(Modifier.height(20.dp))

                                // ✅ BOTÓN MORADO
                                Button(
                                    onClick = {
                                        carritoViewModel.agregarAlCarrito(producto!!)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(62.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF9C55E7)
                                    )
                                ) {
                                    Text(
                                        "Agregar al Carrito",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Spacer(Modifier.height(28.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // ✅ NOTIFICACIÓN VISIBLE SIEMPRE
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1000f),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = mostrarNotificacion,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.padding(top = 28.dp)
        ) {
            Surface(
                color = Color(0xFF9C55E7),
                contentColor = Color.White,
                shape = RoundedCornerShape(50),
                shadowElevation = 10.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, null)
                    Spacer(Modifier.width(8.dp))
                    Text(mensajeNotificacion, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
