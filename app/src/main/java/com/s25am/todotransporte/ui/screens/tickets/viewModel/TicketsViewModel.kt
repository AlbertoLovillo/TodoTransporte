package com.s25am.todotransporte.ui.screens.tickets.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.database.SupabaseClient
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.ui.screens.tickets.wallet.componetsWallet.Tickets
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketsViewModel: ViewModel() {
    private val supabase = SupabaseClient.client

    private val _uiState = MutableStateFlow(TicketUiState())
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    private val _lineas = MutableStateFlow<List<Linea>>(emptyList())
    val lineas: StateFlow<List<Linea>> = _lineas




    init {
        cargarLineas()
        fetchSavedTickets()
    }


    /**
     * Función que carga todas las líneas disponibles desde la base de datos.
     */
    private fun cargarLineas() {
        viewModelScope.launch {
            try {
                val resultado = supabase.from("Linea")
                    .select {
                        order("id", order = Order.ASCENDING)
                    }
                    .decodeList<Linea>()

                _lineas.value = resultado

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    /**
     * Carga inicial de tickets
     */
    private fun fetchSavedTickets() {
        viewModelScope.launch {
            // TODO:cargar aquí los tickets filtrando por el email del usuario logueado

            _uiState.value = TicketUiState(listaTickets = emptyList())
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
    fun addTicket(nuevoTicket: Tickets, precio: Double) {
        if (_uiState.value.saldo >= precio) {

            val nuevaLista = _uiState.value.listaTickets + nuevoTicket
            val nuevoSaldo = _uiState.value.saldo - precio

            _uiState.value = _uiState.value.copy(
                listaTickets = nuevaLista,
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
    fun deleteTicket(ticketAEliminar: Tickets) {
        val listaActualizada = _uiState.value.listaTickets.filter { it.id != ticketAEliminar.id }
        _uiState.value = _uiState.value.copy(listaTickets = listaActualizada)

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