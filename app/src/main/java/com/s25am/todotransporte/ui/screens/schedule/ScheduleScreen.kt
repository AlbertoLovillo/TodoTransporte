package com.s25am.todotransporte.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s25am.todotransporte.ui.screens.bus_map.components.LineListButtom
import com.s25am.todotransporte.ui.screens.schedule.components.AlertDialogParada
import com.s25am.todotransporte.ui.screens.schedule.components.ItemParada
import com.s25am.todotransporte.ui.theme.TodoTransporteTheme

/**
 * Pantalla principal de Horarios.
 * NOTA: El Scaffold y la TopBar se gestionan de forma global en MainActivity.
 */
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = viewModel()
) {
    // Observamos los datos desde el ViewModel
    val lineas by viewModel.lineas.collectAsState()
    val lineaSeleccionada by viewModel.selectedLinea.collectAsState()
    val paradas by viewModel.paradas.collectAsState()
    val proximosBuses by viewModel.proximosBusesParadas.collectAsState()
    val paradaSeleccionada by viewModel.paradaSeleccionada.collectAsState()
    val horariosParada by viewModel.horariosParada.collectAsState()
    val direccionActual by viewModel.direccionActual.collectAsState()
    val paradasConBus by viewModel.paradasConBusEnTiempoReal.collectAsState()

    // Diálogo de detalles de parada (se muestra si hay una parada seleccionada)
    if (paradaSeleccionada != null) {
        AlertDialogParada(
            parada = paradaSeleccionada!!,
            horarios = horariosParada,
            onDismiss = { viewModel.cerrarDialogo() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Indicador de sentido (Ida/Vuelta)
        val textoSentido = if (direccionActual == 0) "Ida" else "Vuelta"
        Text(
            text = "Sentido: $textoSentido",
            modifier = Modifier.padding(start = 16.dp, top = 12.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón para alternar el sentido
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

            // Selector horizontal de líneas
            LazyRow(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lineas) { linea ->
                    LineListButtom(
                        linea = linea,
                        estaSeleccionada = linea.id == lineaSeleccionada?.id,
                        onClick = { viewModel.seleccionarLinea(linea) }
                    )
                }
            }
        }

        // Listado vertical de paradas
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(paradas) { parada ->
                ItemParada(
                    parada = parada,
                    proximoBusHora = proximosBuses[parada.id],
                    tieneBusCerca = paradasConBus.contains(parada.id),
                    onClick = { viewModel.mostrarInfoParada(parada) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SchedulePreview() {
    TodoTransporteTheme {
        ScheduleScreen()
    }
}