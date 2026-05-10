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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s25am.todotransporte.ui.screens.bus_map.components.LineListButtom
import com.s25am.todotransporte.ui.screens.schedule.components.AlertDialogParada
import com.s25am.todotransporte.ui.screens.schedule.components.ItemParada
import com.s25am.todotransporte.ui.theme.TodoTransporteTheme

/**
 * Pantalla de Horarios.
 * Dalton: Se ha añadido fondo blanco y mejorado
 */
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.paradaSeleccionada != null) {
        AlertDialogParada(
            parada = uiState.paradaSeleccionada!!,
            horarios = uiState.horariosParada,
            onDismiss = { viewModel.cerrarDialogo() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Fondo blanco
    ) {
        val textoSentido = if (uiState.direccionActual == 0) "Ida" else "Vuelta"
        
        Text(
            text = "Sentido: $textoSentido",
            modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.alternarDireccion() },
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Cambiar Sentido",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            LazyRow(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp) // Más espacio entre líneas
            ) {
                items(uiState.lineas) { linea ->
                    LineListButtom(
                        linea = linea,
                        estaSeleccionada = linea.id == uiState.selectedLinea?.id,
                        onClick = { viewModel.seleccionarLinea(linea) }
                    )
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(uiState.paradas) { parada ->
                ItemParada(
                    parada = parada,
                    proximoBusHora = uiState.proximosBusesParadas[parada.id],
                    tieneBusCerca = uiState.paradasConBusEnTiempoReal.contains(parada.id),
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