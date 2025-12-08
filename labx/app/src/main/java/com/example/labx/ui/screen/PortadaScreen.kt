package com.example.labx.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labx.R

@Composable
fun PortadaScreen(
    onEntrarClick: () -> Unit,
    onLoginClick: () -> Unit
) {

    val colors = MaterialTheme.colorScheme

    // ✅ Animación muy sutil del logo (solo para dar vida)
    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // ✅ FONDO MUY OSCURO PARA QUE EL LOGO RESALTE
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colors.background,
                        colors.background,
                        colors.surfaceVariant
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ✅ LOGO LIMPIO, SIN ARO, SIN CÍRCULO
            Image(
                painter = painterResource(id = R.drawable.logo_level),
                contentDescription = "Logo Level-Up",
                modifier = Modifier
                    .size(260.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ✅ FRASE COMERCIAL
            Text(
                text = "Tu tienda gamer de confianza",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colors.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(50.dp))

            // ✅ BOTÓN PRINCIPAL — INICIAR SESIÓN
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary
                ),
                elevation = ButtonDefaults.buttonElevation(10.dp)
            ) {
                Text(
                    text = "INICIAR SESIÓN",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ✅ BOTÓN SECUNDARIO — VER CATÁLOGO
            OutlinedButton(
                onClick = onEntrarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "VER CATÁLOGO",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onBackground
                )
            }

            Spacer(modifier = Modifier.height(42.dp))

            // ✅ TEXTO COMERCIAL INFERIOR
            Text(
                text = "🎮 Consolas • PCs Gamer • Accesorios",
                fontSize = 13.sp,
                color = colors.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Compra segura • Envíos rápidos • Soporte real",
                fontSize = 11.sp,
                color = colors.onBackground.copy(alpha = 0.55f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Level-Up App • 2025",
                fontSize = 10.sp,
                color = colors.onSurfaceVariant.copy(alpha = 0.75f)
            )
        }
    }
}
