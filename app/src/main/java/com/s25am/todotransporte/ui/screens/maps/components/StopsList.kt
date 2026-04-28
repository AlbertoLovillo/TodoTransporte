package com.s25am.todotransporte.ui.screens.maps.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada
import com.s25am.todotransporte.ui.screens.maps.MapsViewModel

@Composable
fun StopsList(
    lineas: List<Linea>,
    paradas: List<Parada>,
    lineaSeleccionada: Linea?,
    viewModel: MapsViewModel
) {
    Column(
//        modifier = Modifier.align(Alignment.BottomCenter)
    ) {
        if (lineas.isEmpty()) {
            Text(
                text = "Cargando líneas...",
                color = Color.Red
            )
        } else {
            Text(
                text = "Paradas cargadas: ${paradas.size}",
                modifier = Modifier.padding(8.dp)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
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