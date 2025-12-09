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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.labx.R
import kotlin.math.abs

@Composable
fun PortadaScreen(
    onEntrarClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    val infiniteTransition = rememberInfiniteTransition()

    // 🔥 ZOOM NEON
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // ✨ PULSO DE BRILLO
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800),
            repeatMode = RepeatMode.Reverse
        )
    )

    // 🎮 MICRO VIBRACIÓN GAMER
    val shake by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(120),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
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

            // ✅ LOGO NEON PULSE
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.Cyan.copy(alpha = glow),
                                    Color.Transparent
                                )
                            ),
                            radius = size.width * 0.75f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_level),
                    contentDescription = "Logo Level-Up",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = if (glow > 0.75f) shake else 0f
                        }
                        .alpha(0.95f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tu tienda gamer de confianza",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = colors.onBackground.copy(alpha = 0.82f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary
                )
            ) {
                Text(
                    text = "INICIAR SESIÓN",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

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
