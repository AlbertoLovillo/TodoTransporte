package com.s25am.todotransporte.ui.screens.tickets

import com.s25am.todotransporte.database.data.Billete
import com.s25am.todotransporte.database.data.Linea

data class TicketUiState(
    val lineas: List<Linea> = emptyList(),
    val listaBilletes: List<Billete> = emptyList(),
    val isLoading: Boolean = false,
    val saldo: Double = 0.0,
    val mostrarErrorSaldo: Boolean = false,
    val lineaParaVerEnMapa: String? = null,
    val billeteSeleccionadoId: String? = null,
    val searchText: String = ""
)