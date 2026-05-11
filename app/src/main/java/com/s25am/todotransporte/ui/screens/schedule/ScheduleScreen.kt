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
import com.s25am.todotransporte.ui.screens.bus_map.components.StopsList
import com.s25am.todotransporte.ui.screens.schedule.components.AlertDialogParada
import com.s25am.todotransporte.ui.screens.schedule.components.ItemParada
import com.s25am.todotransporte.ui.theme.TodoTransporteTheme


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
            .background(MaterialTheme.colorScheme.background)
    ) {

        StopsList(
            lineas = uiState.lineas,
            paradas = uiState.paradas,
            lineaSeleccionada = uiState.selectedLinea,
            direccionActual = uiState.direccionActual,
            onAlternarDireccion = { viewModel.alternarDireccion() },
            onSeleccionarLinea = { linea -> viewModel.seleccionarLinea(linea) },
            isMap = false
        )

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(uiState.paradas) { parada ->
                ItemParada(
                    parada = parada,
                    proximoBusHora = uiState.proximosBusesParadas[parada.id],
//                    tieneBusCerca = uiState.paradasConBusEnTiempoReal.contains(parada.id),
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