package com.s25am.todotransporte.ui.screens.wallet.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.ui.screens.wallet.componetsWallet.Tikets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    fun dismissErrorSaldo() {
        _uiState.value = _uiState.value.copy(mostrarErrorSaldo = false)
    }

    /**
     * Funcion para recargarSaldo
     */
    fun recargarSaldo(cantidad: Double) {
        val nuevoSaldo = _uiState.value.saldo + cantidad
        _uiState.value = _uiState.value.copy(saldo = nuevoSaldo)

        viewModelScope.launch {
            // TODO: aquí hay que hacer un UPDATE para el saldo
            println("Saldo recargado localmente: $nuevoSaldo")
        }
    }

    /**
     * Función para añadir un ticket comprado
     */
    fun addTicket(nuevoTicket: Tikets, precio: Double) {
        if (_uiState.value.saldo >= precio) {

            val nuevaLista = _uiState.value.listaTikets + nuevoTicket
            val nuevoSaldo = _uiState.value.saldo - precio

            _uiState.value = _uiState.value.copy(
                listaTikets = nuevaLista,
                saldo = nuevoSaldo
            )

            viewModelScope.launch {
                try {
                    // TODO: aquí debes restar el 'precio' al saldo del usuario y añadir el 'nuevoTicket' en la base de datos.
                    println("Compra realizada: ${nuevoTicket.titulo}. Saldo restante: $nuevoSaldo")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            _uiState.value = _uiState.value.copy(mostrarErrorSaldo = true)
        }
    }

    /**
     * Función para eliminar un ticket (Swipe to dismiss)
     */
    fun deleteTicket(ticketAEliminar: Tikets) {
        val listaActualizada = _uiState.value.listaTikets.filter { it.id != ticketAEliminar.id }
        _uiState.value = _uiState.value.copy(listaTikets = listaActualizada)

        viewModelScope.launch {
            try {
                // TODO: eliminar aquí el ticket de Supabase usando el ID
                // que solo se borre si pertenece al email del usuario actual

                println("Local: Ticket eliminado. Pendiente borrar en DB: ${ticketAEliminar.titulo}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}