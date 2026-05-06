package com.s25am.todotransporte.ui.screens.tickets.viewModel

import com.s25am.todotransporte.ui.screens.tickets.wallet.componetsWallet.Tickets

data class TicketUiState(
    val listaTickets: List<Tickets> = emptyList(),
    val isLoading: Boolean = false,
    val saldo: Double = 5.50,
    val mostrarErrorSaldo: Boolean = false
)