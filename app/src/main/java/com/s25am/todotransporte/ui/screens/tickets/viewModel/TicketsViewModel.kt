package com.s25am.todotransporte.ui.screens.tickets.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.database.SupabaseClient
import com.s25am.todotransporte.database.data.Billete
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Usuario
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TicketsViewModel: ViewModel() {
    private val supabase = SupabaseClient.client

    private val _uiState = MutableStateFlow(TicketUiState())
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    private val _lineas = MutableStateFlow<List<Linea>>(emptyList())
    val lineas: StateFlow<List<Linea>> = _lineas




    init {
        cargarLineas()
        fetchSavedBilletesYSaldo()
    }


    /**
     * 1. FUNCIÓN RECUPERADA: Obtiene el email del usuario logueado actualmente
     */
    private fun obtenerEmailUsuario(): String? {
        return supabase.auth.currentUserOrNull()?.email
    }


    /**
     * Función que carga todas las líneas disponibles desde la base de datos.
     */
    private fun cargarLineas() {
        viewModelScope.launch {
            try {
                val resultado = supabase.from("Linea")
                    .select {
                        order("id", order = io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                    }
                    .decodeList<Linea>()

                _lineas.value = resultado
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /**
     * Carga inicial de billetes y saldo filtrando por el email del usuario
     */
    private fun fetchSavedBilletesYSaldo() {
        val email = obtenerEmailUsuario() ?: return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val usuario = supabase.from("Usuario")
                    .select { filter { eq("email", email) } }
                    .decodeSingleOrNull<Usuario>()

                val saldoActual = usuario?.saldo ?: 0.0

                val misBilletes = supabase.from("Billete")
                    .select { filter { eq("email_usuario", email) } }
                    .decodeList<Billete>()

                _uiState.update {
                    it.copy(
                        listaBilletes = misBilletes,
                        saldo = saldoActual,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun dismissErrorSaldo() {
        _uiState.update { it.copy(mostrarErrorSaldo = false) }
    }


    /**
     * Funcion para recargar Saldo
     */
    fun recargarSaldo(cantidad: Double) {
        val email = obtenerEmailUsuario() ?: return
        val nuevoSaldo = _uiState.value.saldo + cantidad

        _uiState.update { it.copy(saldo = nuevoSaldo) }

        viewModelScope.launch {
            try {
                supabase.from("Usuario").update(
                    {
                        set("saldo", nuevoSaldo)
                    }
                ) {
                    filter { eq("email", email) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /**
     * Función para añadir un ticket comprado
     */
    fun addTicket(nuevoBilleteBase: Billete, precio: Double) {
        val email = obtenerEmailUsuario() ?: return

        if (_uiState.value.saldo >= precio) {
            val billeteParaGuardar = nuevoBilleteBase.copy(email_usuario = email)

            val nuevaLista = _uiState.value.listaBilletes + billeteParaGuardar
            val nuevoSaldo = _uiState.value.saldo - precio

            _uiState.update {
                it.copy(
                    listaBilletes = nuevaLista,
                    saldo = nuevoSaldo
                )
            }

            viewModelScope.launch {
                try {
                    supabase.from("Billete").insert(billeteParaGuardar)

                    // Actualizamos solo el saldo en la tabla Usuario
                    supabase.from("Usuario").update(
                        {
                            set("saldo", nuevoSaldo)
                        }
                    ) {
                        filter { eq("email", email) }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            _uiState.update { it.copy(mostrarErrorSaldo = true) }
        }
    }


    /**
     * Función para eliminar un ticket (Swipe to dismiss)
     */
    fun deleteTicket(billeteAEliminar: Billete) {
        val email = obtenerEmailUsuario() ?: return

        val listaActualizada = _uiState.value.listaBilletes.filter { it.id != billeteAEliminar.id }
        _uiState.update { it.copy(listaBilletes = listaActualizada) }

        viewModelScope.launch {
            try {
                supabase.from("Billete").delete {
                    filter {
                        eq("id", billeteAEliminar.id)
                        eq("email_usuario", email)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}