package com.s25am.todotransporte.ui.screens.tickets.viewModel

import com.s25am.todotransporte.database.data.Billete

data class TicketUiState(
    val listaBilletes: List<Billete> = emptyList(),
    val isLoading: Boolean = false,
    val saldo: Double = 5.50,
    val mostrarErrorSaldo: Boolean = false
)