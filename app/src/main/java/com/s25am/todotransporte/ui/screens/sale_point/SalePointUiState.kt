package com.s25am.todotransporte.ui.screens.sale_point

import com.s25am.todotransporte.database.data.PuntoVenta

data class SalePointUiState(
    val puntosVenta: List<PuntoVenta> = emptyList(),
    val puntoSeleccionado: PuntoVenta? = null
)
