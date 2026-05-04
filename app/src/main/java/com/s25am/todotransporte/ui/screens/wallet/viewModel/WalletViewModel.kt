package com.s25am.todotransporte.ui.screens.wallet.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.ui.screens.wallet.componetsWallet.Tikets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalletViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        fetchSavedTickets()
    }

    /**
     * Carga inicial de tickets
     */
    private fun fetchSavedTickets() {
        viewModelScope.launch {
            // TODO: Cargar aquí los tickets desde Supabase
            //Estaba pensando en usar los email como forma de identificar al usuario
            // para que no aparezcan los billetes a todos

            _uiState.value = WalletUiState(listaTikets = emptyList())
        }
    }

    /**
     * Función para añadir un ticket comprado
     */
    fun addTicket(nuevoTicket: Tikets) {
        val listaActualizada = _uiState.value.listaTikets + nuevoTicket
        _uiState.value = _uiState.value.copy(listaTikets = listaActualizada)

        // Persistencia en segundo plano
        viewModelScope.launch {
            try {
                // TODO: Insertar aquí el nuevoTicket en la tabla de Supabase

                println("Local: Ticket añadido a la lista. Pendiente subir a DB: ${nuevoTicket.titulo}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}