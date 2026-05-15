package com.s25am.todotransporte.ui.screens.bus_map

import android.location.Location
import com.s25am.todotransporte.database.data.Horario
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada
import com.s25am.todotransporte.database.data.PosicionBus

data class BusMapsUiState(
    val lineas: List<Linea> = emptyList(),
    val selectedLinea: Linea? = null,
    val paradas: List<Parada> = emptyList(),
    val paradaSeleccionada: Parada? = null,
    val proximoBusHora: String? = null,
    val horariosParada: List<Horario> = emptyList(),
    val direccionActual: Int = 0,
    val rutaGeojsonActual: String? = null,
    val destino: String? = null,
    val busesEnTiempoReal: List<PosicionBus> = emptyList(),
    val ubicacionUsuario: Location? = null
)
