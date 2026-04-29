package com.s25am.todotransporte.ui.screens.wallet.viewModel

import androidx.lifecycle.ViewModel
import com.s25am.todotransporte.ui.screens.wallet.componets.Tikets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalletViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = WalletUiState(
            listaTikets = listOf(
                Tikets("1", "Billete Sencillo", "Linea 1", "Válido hoy"),
                Tikets("2", "Bono 10 Viajes", "Linea 2", "Final de año"),
                Tikets("3", "Billete Ida y Vuelta", "Linea 3", "Válido hasta 20/05")
            )
        )
    }
}