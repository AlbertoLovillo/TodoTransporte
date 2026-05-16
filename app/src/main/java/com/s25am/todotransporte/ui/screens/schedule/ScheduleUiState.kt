package com.s25am.todotransporte.ui.screens.schedule

import com.s25am.todotransporte.database.data.Horario
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada
import com.s25am.todotransporte.database.data.PosicionBus

data class ScheduleUiState(
    val lineas: List<Linea> = emptyList(),
    val selectedLinea: Linea? = null,
    val paradas: List<Parada> = emptyList(),
    val paradaSeleccionada: Parada? = null,
    val horariosParada: List<Horario> = emptyList(),
    val proximosBusesParadas: Map<Int, String> = emptyMap(),
    val direccionActual: Int = 0,
    val destino: String? = null,
    val busesEnTiempoReal: List<PosicionBus> = emptyList(),
    val paradasConBusEnTiempoReal: Set<Int> = emptySet()
)