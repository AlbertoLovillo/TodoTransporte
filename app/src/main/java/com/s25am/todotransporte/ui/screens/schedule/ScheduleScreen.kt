package com.s25am.todotransporte.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.s25am.todotransporte.ui.screens.bus_map.components.StopsList
import com.s25am.todotransporte.ui.screens.schedule.components.AlertDialogParada
import com.s25am.todotransporte.ui.screens.schedule.components.ItemParada
import com.s25am.todotransporte.ui.theme.GrisFondoClaro


@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ScheduleContent(
        uiState = uiState,
        onAlternarDireccion = { viewModel.alternarDireccion() },
        onSeleccionarLinea = { linea -> viewModel.seleccionarLinea(linea) },
        onMostrarInfoParada = { parada -> viewModel.mostrarInfoParada(parada) },
        onCerrarDialogo = { viewModel.cerrarDialogo() }
    )
}

@Composable
fun ScheduleContent(
    uiState: ScheduleUiState,
    onAlternarDireccion: () -> Unit,
    onSeleccionarLinea: (com.s25am.todotransporte.database.data.Linea) -> Unit,
    onMostrarInfoParada: (com.s25am.todotransporte.database.data.Parada) -> Unit,
    onCerrarDialogo: () -> Unit
) {
    if (uiState.paradaSeleccionada != null) {
        AlertDialogParada(
            parada = uiState.paradaSeleccionada,
            horarios = uiState.horariosParada,
            onDismiss = onCerrarDialogo
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = GrisFondoClaro)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GrisFondoClaro)
        ) {
            StopsList(
                lineas = uiState.lineas,
                paradas = uiState.paradas,
                lineaSeleccionada = uiState.selectedLinea,
                direccionActual = uiState.direccionActual,
                onAlternarDireccion = onAlternarDireccion,
                onSeleccionarLinea = onSeleccionarLinea,
                isMap = false
            )
        }

        Text(
            text = "Paradas de la línea",
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
            color = Color.Black
        )

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(uiState.paradas) { parada ->
                ItemParada(
                    parada = parada,
                    proximoBusHora = uiState.proximosBusesParadas[parada.id],
                    onClick = { onMostrarInfoParada(parada) }
                )
            }
        }
    }
}