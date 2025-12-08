package com.example.labx.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import coil.compose.AsyncImage
import com.example.labx.domain.model.Producto
import com.example.labx.domain.repository.RepositorioProductos
import com.example.labx.ui.viewmodel.CarritoViewModel
import com.example.labx.ui.viewmodel.ProductoViewModel
import com.example.labx.ui.viewmodel.UsuarioViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.core.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repositorioLocal: RepositorioProductos,
    usuarioViewModel: UsuarioViewModel,
    carritoViewModel: CarritoViewModel,
    onProductoClick: (Int) -> Unit,
    onCarritoClick: () -> Unit,
    onMiCuentaClick: () -> Unit,
    onIrLogin: () -> Unit,
    onVolverPortada: () -> Unit
) {

    val productoViewModel: ProductoViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProductoViewModel(repositorioLocal) as T
            }
        }
    )

    val uiState by productoViewModel.uiState.collectAsState()
    val itemsCarrito by carritoViewModel.itemsCarrito.collectAsState()
    val authState by usuarioViewModel.authState.collectAsState()
    val usuarioActual = authState.usuarioActual

    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }

    val productosFiltrados = remember(uiState.productos, textoBusqueda, categoriaSeleccionada) {
        uiState.productos.filter {
            (textoBusqueda.isBlank() ||
                    it.nombre.contains(textoBusqueda, true) ||
                    it.descripcion.contains(textoBusqueda, true)) &&
                    (categoriaSeleccionada == null || it.categoria == categoriaSeleccionada)
        }
    }

    val categorias = remember(uiState.productos) {
        uiState.productos.map { it.categoria }.distinct()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Level-Up Store") },
                navigationIcon = {
                    IconButton(onClick = onVolverPortada) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (usuarioActual == null) onIrLogin() else onMiCuentaClick()
                    }) {
                        Icon(Icons.Default.AccountCircle, null)
                    }
                    BadgedBox(
                        badge = {
                            if (itemsCarrito.isNotEmpty()) {
                                Badge { Text("${itemsCarrito.size}") }
                            }
                        }
                    ) {
                        IconButton(onClick = onCarritoClick) {
                            Icon(Icons.Default.ShoppingCart, null)
                        }
                    }
                }
            )
        }
    ) { padding ->

        // ✅ FONDO CLARO
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
                uiState.estaCargando -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                uiState.productos.isEmpty() -> {
                    Text(
                        "No hay productos disponibles",
                        Modifier.align(Alignment.Center),
                        color = Color.DarkGray
                    )
                }

                else -> {
                    Column {

                        // ✅ BUSCADOR LEGIBLE
                        OutlinedTextField(
                            value = textoBusqueda,
                            onValueChange = { textoBusqueda = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            placeholder = {
                                Text(
                                    "Buscar productos…",
                                    color = Color.Gray
                                )
                            },
                            textStyle = LocalTextStyle.current.copy(
                                color = Color(0xFF2B1454)
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color(0xFF6A3FC9)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF9C55E7),
                                unfocusedBorderColor = Color(0xFFB39DDB),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = Color(0xFF6A3FC9)
                            )
                        )

                        // ✅ FILTROS LEGIBLES
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = categoriaSeleccionada == null,
                                onClick = { categoriaSeleccionada = null },
                                label = { Text("Todos") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF9C55E7),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFE0E0E0),
                                    labelColor = Color(0xFF2B1454)
                                )
                            )

                            categorias.forEach { categoria ->
                                FilterChip(
                                    selected = categoriaSeleccionada == categoria,
                                    onClick = { categoriaSeleccionada = categoria },
                                    label = { Text(categoria) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF9C55E7),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFFE0E0E0),
                                        labelColor = Color(0xFF2B1454)
                                    )
                                )
                            }
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            items(productosFiltrados) { producto ->
                                ProductoCardPremiumMorado(producto) {
                                    onProductoClick(producto.id)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===============================
//   CARD PREMIUM LILA + PRECIO
// ===============================
@Composable
fun ProductoCardPremiumMorado(
    producto: Producto,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val imageModel = remember(producto.imagenUrl) {
        if (producto.imagenUrl.startsWith("http")) {
            producto.imagenUrl
        } else {
            val resId = context.resources.getIdentifier(
                producto.imagenUrl,
                "drawable",
                context.packageName
            )
            if (resId != 0) resId else android.R.drawable.ic_menu_gallery
        }
    }

    // Lila suave de la card
    val lilaCard = Color(0xFFE6D9FF)

    // Fondo de imagen: lila → morado
    val fondoImagenOscuro = Brush.verticalGradient(
        listOf(
            Color(0xFFD1BFFF),
            Color(0xFF7B4AE2)
        )
    )

    // Animación suave del precio
    val animacionPrecio = rememberInfiniteTransition(label = "precio_card")
    val escalaPrecio by animacionPrecio.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = lilaCard),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column {

            // Zona imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .background(fondoImagenOscuro),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    contentScale = ContentScale.Fit
                )

                // Precio premium animado (oscuro)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .graphicsLayer {
                            scaleX = escalaPrecio
                            scaleY = escalaPrecio
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF6A3FC9),
                                    Color(0xFF4C2A85)
                                )
                            )
                        )
                        .shadow(10.dp, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 7.dp)
                ) {
                    Text(
                        producto.precioFormateado(),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }

            // Texto de la card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(lilaCard)
                    .padding(14.dp)
            ) {
                Text(
                    producto.nombre,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B1454),
                    maxLines = 2
                )

                Text(
                    producto.categoria,
                    color = Color(0xFF4C2A85),
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Ver detalle",
                        color = Color(0xFF6A3FC9),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF6A3FC9)
                    )
                }
            }
        }
    }
}
