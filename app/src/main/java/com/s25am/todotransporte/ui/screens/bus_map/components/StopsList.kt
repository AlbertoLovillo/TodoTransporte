package com.s25am.todotransporte.ui.screens.bus_map.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
                    onAlternarDireccion()
                },
                modifier = Modifier
                    .padding(end = 12.dp)
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
