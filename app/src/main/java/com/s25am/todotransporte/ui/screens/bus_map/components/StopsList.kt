package com.s25am.todotransporte.ui.screens.bus_map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada
import com.s25am.todotransporte.ui.screens.bus_map.MapsViewModel

@Composable
fun StopsList(
    lineas: List<Linea>,
    paradas: List<Parada>,
    lineaSeleccionada: Linea?,
    direccionActual: Int,
    viewModel: MapsViewModel
) {


    Column() {
        if (lineas.isEmpty()) {
            Text(
                text = "Cargando líneas...",
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            val textoSentido = if (direccionActual == 0) "Ida" else "Vuelta"
            Text(
                text = "Paradas: ${paradas.size} | Sentido: $textoSentido",
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.labelLarge
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp, start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = { viewModel.alternarDireccion() },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Cambiar Sentido",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                LazyRow(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lineas) { linea ->
                        LineListButtom(
                            linea = linea,
                            estaSeleccionada = (linea.id == lineaSeleccionada?.id),
                            onClick = { viewModel.seleccionarLinea(linea) }
                        )
                    }
                }
            }
        }
    }
}