package com.example.labx.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.labx.data.repository.ProductoRepositoryImpl
import com.example.labx.domain.model.Producto
import com.example.labx.ui.viewmodel.CarritoViewModel
import com.example.labx.ui.viewmodel.ProductoViewModel
import com.example.labx.ui.viewmodel.ProductoViewModelFactory
import com.example.labx.ui.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    productoRepository: ProductoRepositoryImpl,
    usuarioViewModel: UsuarioViewModel,
    carritoViewModel: CarritoViewModel, // ✅ AHORA USAMOS VIEWMODEL
    onProductoClick: (Int) -> Unit,
    onCarritoClick: () -> Unit,
    onMiCuentaClick: () -> Unit,
    onIrLogin: () -> Unit,
    onVolverPortada: () -> Unit
) {

    val productoViewModel: ProductoViewModel = viewModel(
        factory = ProductoViewModelFactory(productoRepository)
    )

    val uiState by productoViewModel.uiState.collectAsState()

    // ✅ AHORA EL CARRITO VIENE DEL VIEWMODEL
    val itemsCarrito by carritoViewModel.itemsCarrito.collectAsState()

    val authState by usuarioViewModel.authState.collectAsState()
    val usuarioActual = authState.usuarioActual

    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }

    val productosFiltrados = remember(uiState.productos, textoBusqueda, categoriaSeleccionada) {
        uiState.productos.filter { producto ->
            val coincideTexto = textoBusqueda.isBlank() ||
                    producto.nombre.contains(textoBusqueda, ignoreCase = true) ||
                    producto.descripcion.contains(textoBusqueda, ignoreCase = true)

            val coincideCategoria = categoriaSeleccionada == null ||
                    producto.categoria == categoriaSeleccionada

            coincideTexto && coincideCategoria
        }
    }

    val categorias = remember(uiState.productos) {
        uiState.productos.map { it.categoria }.distinct().sorted()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos Disponibles") },
                navigationIcon = {
                    IconButton(onClick = onVolverPortada) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {

                    IconButton(
                        onClick = {
                            if (usuarioActual == null) onIrLogin() else onMiCuentaClick()
                        }
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = if (usuarioActual == null) "Iniciar Sesión" else "Mi Cuenta"
                        )
                    }

                    BadgedBox(
                        badge = {
                            if (itemsCarrito.isNotEmpty()) {
                                Badge { Text("${itemsCarrito.size}") }
                            }
                        }
                    ) {
                        IconButton(onClick = onCarritoClick) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                        }
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
                uiState.estaCargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { productoViewModel.cargarProductos() }) {
                            Text("Reintentar")
                        }
                    }
                }

                uiState.productos.isEmpty() -> {
                    Text(
                        "No hay productos disponibles",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {

                    Column(Modifier.fillMaxSize()) {

                        OutlinedTextField(
                            value = textoBusqueda,
                            onValueChange = { textoBusqueda = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Buscar productos...") },
                            leadingIcon = { Icon(Icons.Default.Search, null) },
                            trailingIcon = {
                                if (textoBusqueda.isNotEmpty()) {
                                    IconButton(onClick = { textoBusqueda = "" }) {
                                        Icon(Icons.Default.Clear, null)
                                    }
                                }
                            },
                            singleLine = true
                        )

                        if (categorias.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                FilterChip(
                                    selected = categoriaSeleccionada == null,
                                    onClick = { categoriaSeleccionada = null },
                                    label = { Text("Todos") }
                                )

                                categorias.forEach { categoria ->
                                    FilterChip(
                                        selected = categoriaSeleccionada == categoria,
                                        onClick = {
                                            categoriaSeleccionada =
                                                if (categoriaSeleccionada == categoria) null else categoria
                                        },
                                        label = { Text(categoria) }
                                    )
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(productosFiltrados) { producto ->
                                ProductoCard(
                                    producto = producto,
                                    onClick = { onProductoClick(producto.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
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
                producto.imagenUrl,
                "drawable",
                context.packageName
            )

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(if (imageResId != 0) imageResId else null)
                    .crossfade(true)
                    .build(),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(producto.nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(producto.categoria, fontSize = 12.sp)
                Text(
                    producto.precioFormateado(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
