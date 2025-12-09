package com.example.labx.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labx.domain.model.Producto
import com.example.labx.data.local.entity.UsuarioEntity
import com.example.labx.ui.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    productos: List<Producto>,
    usernameAdmin: String,
    onAgregarProducto: () -> Unit,
    onEditarProducto: (Producto) -> Unit,
    onEliminarProducto: (Producto) -> Unit,
    onCerrarSesion: () -> Unit,
    usuarioViewModel: UsuarioViewModel,
    onVolver: () -> Unit
) {

    var mostrarDialogoEliminarProducto by remember { mutableStateOf<Producto?>(null) }
    var usuarioSeleccionado by remember { mutableStateOf<UsuarioEntity?>(null) }
    var usuarioAEliminar by remember { mutableStateOf<UsuarioEntity?>(null) }
    var pestanaSeleccionada by remember { mutableStateOf(0) }
    var mostrarUsuarios by remember { mutableStateOf(false) }

    val usuarios by usuarioViewModel.listaUsuarios.collectAsState()

    LaunchedEffect(Unit) { usuarioViewModel.cargarUsuarios() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel Admin")
                        Text("Sesión: $usernameAdmin", fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.Default.ExitToApp, null)
                    }
                }
            )
        },
        floatingActionButton = {
            if (pestanaSeleccionada == 0) {
                FloatingActionButton(onClick = onAgregarProducto) {
                    Icon(Icons.Default.Add, null)
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            TabRow(selectedTabIndex = pestanaSeleccionada) {
                Tab(
                    selected = pestanaSeleccionada == 0,
                    onClick = { pestanaSeleccionada = 0 },
                    text = { Text("Productos") }
                )
                Tab(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    text = { Text("Estadísticas") }
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { mostrarUsuarios = !mostrarUsuarios },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(if (mostrarUsuarios) "Ocultar Usuarios" else "Ver Usuarios Registrados")
            }

            if (mostrarUsuarios) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                ) {
                    items(usuarios) { usuario ->
                        UsuarioItem(
                            usuario = usuario,
                            onEditar = { usuarioSeleccionado = it },
                            onEliminar = { usuarioAEliminar = it }
                        )
                    }
                }
            }

            when (pestanaSeleccionada) {
                0 -> ListaProductos(
                    productos = productos,
                    onEditarProducto = onEditarProducto,
                    onEliminarProducto = { mostrarDialogoEliminarProducto = it }
                )

                1 -> EstadisticasPanel(productos)
            }
        }
    }

    /* ----------- DIÁLOGO EDITAR USUARIO ---------- */
    usuarioSeleccionado?.let { user ->
        var nombre by remember { mutableStateOf(user.nombreCompleto) }
        var telefono by remember { mutableStateOf(user.telefono ?: "") }
        var direccion by remember { mutableStateOf(user.direccion ?: "") }

        AlertDialog(
            onDismissRequest = { usuarioSeleccionado = null },
            title = { Text("Editar Usuario") },
            text = {
                Column {
                    OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") })
                    OutlinedTextField(telefono, { telefono = it }, label = { Text("Teléfono") })
                    OutlinedTextField(direccion, { direccion = it }, label = { Text("Dirección") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val editado = user.copy(
                        nombreCompleto = nombre,
                        telefono = telefono,
                        direccion = direccion
                    )
                    usuarioViewModel.actualizarUsuario(editado)
                    usuarioSeleccionado = null
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { usuarioSeleccionado = null }) { Text("Cancelar") }
            }
        )
    }

    /* ----------- DIÁLOGO ELIMINAR USUARIO ---------- */
    usuarioAEliminar?.let {
        AlertDialog(
            onDismissRequest = { usuarioAEliminar = null },
            title = { Text("Eliminar Usuario") },
            text = { Text("¿Seguro que deseas eliminar este usuario?") },
            confirmButton = {
                TextButton(onClick = {
                    usuarioViewModel.eliminarUsuario(it)
                    usuarioAEliminar = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { usuarioAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    /* ----------- DIÁLOGO ELIMINAR PRODUCTO ---------- */
    mostrarDialogoEliminarProducto?.let {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminarProducto = null },
            title = { Text("Eliminar Producto") },
            text = { Text("¿Eliminar ${it.nombre}?") },
            confirmButton = {
                TextButton(onClick = {
                    onEliminarProducto(it)
                    mostrarDialogoEliminarProducto = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoEliminarProducto = null
                }) { Text("Cancelar") }
            }
        )
    }
}

/* ================= USUARIO ITEM ================= */

@Composable
fun UsuarioItem(
    usuario: UsuarioEntity,
    onEliminar: (UsuarioEntity) -> Unit,
    onEditar: (UsuarioEntity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("👤 ${usuario.nombreCompleto}", fontWeight = FontWeight.Bold)
            Text("📧 ${usuario.email}")
            Text("📱 ${usuario.telefono}")
            Text("🏠 ${usuario.direccion}")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onEditar(usuario) }) {
                    Icon(Icons.Default.Edit, null)
                }
                IconButton(onClick = { onEliminar(usuario) }) {
                    Icon(Icons.Default.Delete, null)
                }
            }
        }
    }
}
/* ================= PRODUCTOS ================= */

@Composable
fun ListaProductos(
    productos: List<Producto>,
    onEditarProducto: (Producto) -> Unit,
    onEliminarProducto: (Producto) -> Unit
) {
    if (productos.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay productos disponibles.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos) { producto ->
                AdminProductoCard(
                    producto = producto,
                    onEditar = { onEditarProducto(producto) },
                    onEliminar = { onEliminarProducto(producto) }
                )
            }
        }
    }
}

@Composable
fun AdminProductoCard(
    producto: Producto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    producto.categoria,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Stock: ${producto.stock} | $${producto.precio.toInt()}")
            }
            Row {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}

/* ================= ESTADÍSTICAS ================= */

@Composable
fun EstadisticasPanel(productos: List<Producto>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EstadisticaCard("Total Productos", productos.size.toString(), Icons.Default.ShoppingCart)
        EstadisticaCard("Stock Total", productos.sumOf { it.stock }.toString(), Icons.Default.Star)
        EstadisticaCard(
            "Valor Inventario",
            "$${productos.sumOf { it.precio * it.stock }}",
            Icons.Default.Star
        )
        EstadisticaCard(
            "Categorías",
            productos.map { it.categoria }.distinct().size.toString(),
            Icons.Default.Info
        )
    }
}

@Composable
fun EstadisticaCard(
    titulo: String,
    valor: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(titulo, fontSize = 14.sp)
                Text(valor, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            Icon(icono, contentDescription = null, modifier = Modifier.size(48.dp))
        }
    }
}
