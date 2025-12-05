package com.example.labx.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labx.ui.viewmodel.CarritoViewModel
import com.example.labx.utils.PdfGenerator
import kotlinx.coroutines.delay

@Composable
fun PagoExitosoScreen(
    carritoViewModel: CarritoViewModel,
    idTransaccion: String,
    onVolverInicio: () -> Unit
) {
    val orden = carritoViewModel.ordenFinal

    val escala = remember { Animatable(0f) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        delay(300)
        escala.animateTo(1f, tween(600))
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Button(
                onClick = onVolverInicio,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Home,
                    null
                ); Spacer(Modifier.width(8.dp)); Text("Volver al Inicio")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(escala.value)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50)), contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(Modifier.height(16.dp)); Text(
            "¡Compra Exitosa!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        ); Text("Tu pedido ha sido confirmado", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(32.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                        0.3f
                    )
                ), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "BOLETA ELECTRÓNICA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ); Text(orden?.fecha ?: "", fontSize = 12.sp)
                    }
                    Divider(Modifier.padding(vertical = 12.dp))
                    ItemBoleta("Cliente", orden?.cliente ?: "Usuario")
                    ItemBoleta("ID Transacción", idTransaccion, true)
                    Divider(Modifier.padding(vertical = 12.dp))
                    Text(
                        "Detalle:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    orden?.items?.forEach { item ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${item.cantidad}x ${item.producto.nombre}",
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            ); Text(formatearPrecio(item.subtotal), fontSize = 14.sp)
                        }
                    }
                    Divider(Modifier.padding(vertical = 12.dp))

                    ItemBoleta("Subtotal", formatearPrecio(orden?.subtotal ?: 0.0))
                    if (orden?.montoDescuento ?: 0.0 > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Descuento Duoc", fontSize = 14.sp, color = Color(0xFF4CAF50))
                            Text(
                                "-${formatearPrecio(orden?.montoDescuento ?: 0.0)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOTAL PAGADO", fontWeight = FontWeight.Black); Text(
                        formatearPrecio(
                            orden?.totalFinal ?: 0.0
                        ), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary
                    )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = { orden?.let { PdfGenerator.generarBoletaPdf(context, it) } },
                modifier = Modifier.fillMaxWidth(),
                enabled = orden != null
            ) {
                Icon(
                    Icons.Default.Share,
                    null,
                    modifier = Modifier.size(18.dp)
                ); Spacer(Modifier.width(8.dp)); Text("Descargar Boleta Oficial (PDF)")
            }
        }
    }
}

@Composable
fun ItemBoleta(label: String, valor: String, esMono: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(
        valor,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = if (esMono) FontFamily.Monospace else FontFamily.Default
    )
    }
}