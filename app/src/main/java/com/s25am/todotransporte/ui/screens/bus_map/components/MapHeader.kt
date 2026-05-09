package com.s25am.todotransporte.ui.screens.bus_map.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.R
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.ui.theme.Negro
import kotlinx.coroutines.delay

@Composable
fun MapHeader(
    linea: Linea?,
    destino: String?,
    modifier: Modifier = Modifier
) {
    var visible by remember(linea, destino) { mutableStateOf(true) }

    LaunchedEffect(linea, destino) {
        visible = true
        delay(5000)
        visible = false
    }

    AnimatedVisibility(
        visible = visible,
        // 1. ANIMACIÓN DE ENTRADA: Aparece (Fade) + Sube desde arriba (Slide)
        enter = fadeIn(animationSpec = tween(durationMillis = 1000)) +
                slideInVertically(
                    initialOffsetY = { -it }, // 'it' es la altura, -it hace que venga desde arriba
                    animationSpec = tween(durationMillis = 1000)
                ),
        // 2. ANIMACIÓN DE SALIDA: Desaparece (Fade) + Baja hacia arriba (Slide)
        exit = fadeOut(animationSpec = tween(durationMillis = 1000)) +
                slideOutVertically(
                    targetOffsetY = { -it }, // Se va hacia arriba para despejar el mapa
                    animationSpec = tween(durationMillis = 1000)
                ),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Círculo con el código de línea
                Surface(
                    color = colorResource(id = R.color.RojoP),
                    shape = CircleShape,
                    modifier = Modifier.size(45.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = linea?.codigo ?: "?",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = linea?.nombre ?: "Seleccione una línea",
                        color = Negro,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}