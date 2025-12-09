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

/* =========================
   🎨 PALETA DARK GAMER
   ========================= */

private val Onyx = Color(0xFF131515)
private val Graphite = Color(0xFF2B2C28)
private val Verdigris = Color(0xFF339989)
private val PearlAqua = Color(0xFF7DE2D1)
private val Snow = Color(0xFFFFFAFB)

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
        containerColor = Onyx,
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto", color = Snow) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Onyx),
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, null, tint = Snow)
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Onyx)
                .padding(padding)
        ) {

            when {
                estaCargando -> {
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center),
                        color = Verdigris
                    )
                }

                producto == null -> {
                    Text(
                        "Producto no encontrado",
                        color = Snow,
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
                    ) {

                        // ✅ HERO DARK CON VERDIGRIS
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Graphite,
                                            Onyx
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

                        // ✅ PRECIO FLOTANTE VERDIGRIS
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
                                    .background(Verdigris)
                                    .shadow(18.dp, RoundedCornerShape(22.dp))
                                    .padding(horizontal = 26.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    producto!!.precioFormateado(),
                                    color = Snow,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                            }
                        }

                        // ✅ CARD GRAPHITE
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-12).dp),
                            shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                            colors = CardDefaults.cardColors(containerColor = Graphite),
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
                                    color = Snow,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    producto!!.categoria,
                                    color = PearlAqua,
                                    fontSize = 13.sp
                                )

                                Divider(color = Verdigris)

                                Text(
                                    "Descripción",
                                    fontWeight = FontWeight.Bold,
                                    color = Snow
                                )

                                Text(
                                    producto!!.descripcion,
                                    color = PearlAqua,
                                    fontSize = 14.sp
                                )

                                Spacer(Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        carritoViewModel.agregarAlCarrito(producto!!)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(62.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Verdigris
                                    )
                                ) {
                                    Text(
                                        "Agregar al Carrito",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Snow
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

    // ✅ NOTIFICACIÓN SUPERIOR
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
                color = Verdigris,
                contentColor = Snow,
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
