package com.example.labx.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labx.ui.viewmodel.CarritoEvento
import com.example.labx.ui.viewmodel.CarritoViewModel
import com.example.labx.ui.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    carritoViewModel: CarritoViewModel,
    usuarioViewModel: UsuarioViewModel,
    onVolverClick: () -> Unit,
    onPagoExitoso: (String) -> Unit
) {

    val items by carritoViewModel.itemsCarrito.collectAsState()
    val subtotal by carritoViewModel.totalCarrito.collectAsState()
    val estaProcesando by carritoViewModel.estaProcesando.collectAsState()
    val authState by usuarioViewModel.authState.collectAsState()
    val usuario = authState.usuarioActual
    val esDuoc = usuario?.esDuoc == true
    val descuento = if (esDuoc) subtotal * 0.10 else 0.0
    val totalFinal = subtotal - descuento
    var nombre by remember { mutableStateOf(usuario?.nombreCompleto ?: "") }
    var direccion by remember { mutableStateOf(usuario?.direccion ?: "") }
    var telefono by remember { mutableStateOf(usuario?.telefono ?: "") }
    var metodoPago by remember { mutableStateOf("Debito") }
    var numeroTarjeta by remember { mutableStateOf("") }
    var fechaVencimiento by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    val esEnvioValido = nombre.length > 3 && direccion.length > 5 && telefono.length >= 8

    val esTarjetaValida = if (metodoPago == "Debito") {
        numeroTarjeta.length == 16 && fechaVencimiento.length == 5 && cvv.length == 3
    } else true

    LaunchedEffect(Unit) {
        carritoViewModel.eventos.collect { evento ->
            if (evento is CarritoEvento.NavegarAPagoExitoso) {
                onPagoExitoso(evento.idTransaccion)
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Finalizar Compra") },
                    navigationIcon = {
                        IconButton(
                            onClick = onVolverClick,
                            enabled = !estaProcesando
                        ) { Icon(Icons.Default.ArrowBack, "Volver") }
                    }
                )
            },
            bottomBar = {
                Surface(shadowElevation = 16.dp, color = MaterialTheme.colorScheme.surface) {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .navigationBarsPadding()
                    ) {

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total a Pagar:", fontWeight = FontWeight.Bold)

                            Text(
                                formatearPrecio(totalFinal),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                carritoViewModel.procesarPago(
                                    nombre,
                                    direccion,
                                    metodoPago,
                                    esDuoc
                                )
                            },
                            enabled = !estaProcesando && esEnvioValido && esTarjetaValida,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (estaProcesando) {
                                Text("Procesando...")
                            } else {
                                Text("CONFIRMAR PAGO")
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                CardFormulario("Datos de Facturación") {

                    CampoTexto(
                        nombre,
                        { nombre = it },
                        "Razón Social / Nombre",
                        Icons.Default.Person
                    )

                    CampoTexto(
                        direccion,
                        { direccion = it },
                        "Dirección",
                        Icons.Default.Home
                    )

                    CampoTexto(
                        telefono,
                        { telefono = it },
                        "Teléfono",
                        Icons.Default.Phone,
                        KeyboardType.Phone
                    )
                }

                CardFormulario("Método de Pago") {

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        OpcionPago(
                            "Débito",
                            metodoPago == "Debito",
                            Modifier.weight(1f)
                        ) { metodoPago = "Debito" }

                        OpcionPago(
                            "Transferencia",
                            metodoPago == "Transf",
                            Modifier.weight(1f)
                        ) { metodoPago = "Transf" }
                    }

                    Spacer(Modifier.height(16.dp))

                    AnimatedVisibility(visible = metodoPago == "Debito") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                            OutlinedTextField(
                                value = numeroTarjeta,
                                onValueChange = {
                                    if (it.length <= 16 && it.all { c -> c.isDigit() }) {
                                        numeroTarjeta = it
                                    }
                                },
                                label = { Text("Número Tarjeta") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Lock, null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                placeholder = { Text("16 dígitos") }
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                                OutlinedTextField(
                                    value = fechaVencimiento,
                                    onValueChange = { if (it.length <= 5) fechaVencimiento = it },
                                    label = { Text("MM/AA") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = cvv,
                                    onValueChange = {
                                        if (it.length <= 3 && it.all { c -> c.isDigit() }) cvv = it
                                    },
                                    label = { Text("CVV") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = metodoPago == "Transf") {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)
                            )
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                DatoBanco("Banco:", "Banco Estado")
                                DatoBanco("Tipo:", "Cuenta Vista")
                                DatoBanco("N°:", "12345678-9")
                                DatoBanco("RUT:", "76.543.210-K")
                                DatoBanco("Correo:", "pagos@levelup.cl")
                            }
                        }
                    }
                }

                CardFormulario("Resumen del Pedido") {

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        items.forEach { item ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "${item.cantidad}x ${item.producto.nombre}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(formatearPrecio(item.subtotal))
                            }
                        }

                        Divider(Modifier.padding(vertical = 8.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal Neto")
                            Text(formatearPrecio(subtotal))
                        }

                        if (descuento > 0) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Descuento DUOC (10%)", color = Color(0xFF4CAF50))
                                Text("-${formatearPrecio(descuento)}", color = Color(0xFF4CAF50))
                            }
                        }

                        Divider(Modifier.padding(vertical = 8.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TOTAL A PAGAR", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(formatearPrecio(totalFinal), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(Modifier.height(60.dp))
            }
        }

        AnimatedVisibility(
            visible = estaProcesando,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.8f))
                    .clickable(enabled = false) {}
            ) {
                Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))
                    Text("Procesando pago seguro...", color = Color.White)
                }
            }
        }
    }
}

/* ================= HELPERS ================= */

@Composable
fun CardFormulario(titulo: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun CampoTexto(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
fun OpcionPago(
    texto: String,
    seleccionado: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    FilterChip(
        selected = seleccionado,
        onClick = onSelect,
        label = { Text(texto, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        leadingIcon = { if (seleccionado) Icon(Icons.Default.Check, null) },
        modifier = modifier
    )
}

@Composable
fun DatoBanco(label: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp)
        Text(valor, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
    }
}
