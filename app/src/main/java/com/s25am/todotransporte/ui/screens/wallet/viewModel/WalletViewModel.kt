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
            // TODO:cargar aquí los tickets filtrando por el email del usuario logueado

            _uiState.value = WalletUiState(listaTikets = emptyList())
        }
    }

    /**
     * Función para añadir un ticket comprado
     */
    fun addTicket(nuevoTicket: Tikets) {
        val listaActualizada = _uiState.value.listaTikets + nuevoTicket
        _uiState.value = _uiState.value.copy(listaTikets = listaActualizada)

        viewModelScope.launch {
            try {
                // TODO:insertar aquí el nuevoTicket vinculado al email del usuario
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Función para eliminar un ticket (Swipe to dismiss)
     */
    fun deleteTicket(ticketAEliminar: Tikets) {
        // Borrado local inmediato para que la UI se actualice rápido
        val listaActualizada = _uiState.value.listaTikets.filter { it.id != ticketAEliminar.id }
        _uiState.value = _uiState.value.copy(listaTikets = listaActualizada)

        viewModelScope.launch {
            try {
                // TODO: eliminar aquí el ticket de Supabase usando el ID
                // Asegúrate de que solo se borre si pertenece al email del usuario actual

                println("Local: Ticket eliminado. Pendiente borrar en DB: ${ticketAEliminar.titulo}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}