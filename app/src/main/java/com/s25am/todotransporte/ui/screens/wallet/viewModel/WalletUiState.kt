package com.s25am.todotransporte.ui.screens.wallet.viewModel

import com.s25am.todotransporte.ui.screens.wallet.componets.Tikets

data class WalletUiState(
    val listaTikets: List<Tikets> = emptyList(),
    val isLoading: Boolean = false
)