package com.s25am.todotransporte.ui.screens.bus_map.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.R
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada
import com.s25am.todotransporte.ui.screens.bus_map.BusMapsViewModel

@Composable
fun StopsList(
    lineas: List<Linea>,
    paradas: List<Parada>,
    lineaSeleccionada: Linea?,
    direccionActual: Int,
    viewModel: BusMapsViewModel
) {
    val textoSentido = if (direccionActual == 0) "Ida" else "Vuelta"

    // VARIABLE PARA CONTROLAR EL GIRO
    var rotacionTarget by remember { mutableStateOf(0f) }

    //Rotacion
    val anguloAnimado by animateFloatAsState(
        targetValue = rotacionTarget,
        animationSpec = tween(durationMillis = 500) // Tarda medio segundo en girar
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 1. BARRA DE INFORMACIÓN
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
                Text(
                    text = "Paradas: ${paradas.size} | $textoSentido",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }

        // 2. FILA DE BOTONES
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BOTÓN DE CAMBIO DE SENTIDO
            IconButton(
                onClick = {
                    rotacionTarget += 360f
                    viewModel.alternarDireccion() },
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clip(CircleShape)
                    .rotate(anguloAnimado) // con esto gira
                    .background(colorResource(id = R.color.RojoP))
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Cambiar Sentido",
                    tint = Color.White
                )
            }

            // LISTA DE LÍNEAS
            LazyRow(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(lineas) { linea ->
                    LineListButtom(
                        linea = linea,
                        estaSeleccionada = (linea.id == lineaSeleccionada?.id),
                        onClick = {

                            viewModel.NombreLinea(linea)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}