package com.s25am.todotransporte.ui.screens.sale_point

import android.location.Location
import com.s25am.todotransporte.database.data.PuntoVenta

data class SalePointUiState(
    val puntosVenta: List<PuntoVenta> = emptyList(),
    val puntoSeleccionado: PuntoVenta? = null,
    val ubicacionUsuario: Location? = null
    )
