package com.s25am.todotransporte.ui.screens.bus_map.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.R
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada

@Composable
fun StopsList(
    lineas: List<Linea>,
    paradas: List<Parada>,
    lineaSeleccionada: Linea?,
    direccionActual: Int,
    onAlternarDireccion: () -> Unit,
    onSeleccionarLinea: (Linea) -> Unit
) {
    var rotacionTarget by remember { mutableStateOf(0f) }
    val anguloAnimado by animateFloatAsState(
        targetValue = rotacionTarget,
        animationSpec = tween(durationMillis = 500)
    )

    val textoSentido = if (direccionActual == 0) "Ida" else "Vuelta"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Surface(
            color = Color(0xFF1A1A1A),
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (lineas.isEmpty()) {
                    Text("Cargando líneas...", style = MaterialTheme.typography.bodySmall, color = Color.Red)
                } else {
                    Text(
                        text = "Paradas: ${paradas.size} | $textoSentido",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    rotacionTarget += 360f
                    onAlternarDireccion()
                },
                modifier = Modifier
                    .padding(end = 12.dp)
                    .rotate(anguloAnimado)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.RojoP)) // Tu color rojo
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Cambiar Sentido",
                    tint = Color.White
                )
            }

            LazyRow(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(lineas) { linea ->
                    LineListButtom(
                        linea = linea,
                        estaSeleccionada = (linea.id == lineaSeleccionada?.id),
                        onClick = { onSeleccionarLinea(linea) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}