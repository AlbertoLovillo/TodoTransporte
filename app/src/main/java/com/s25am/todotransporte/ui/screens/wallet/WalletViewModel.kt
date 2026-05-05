package com.s25am.todotransporte.ui.screens.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.database.SupabaseClient
import com.s25am.todotransporte.database.data.Ticket
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar la lógica de la cartera y la compra de billetes.
 */
class WalletViewModel : ViewModel() {
    private val supabase = SupabaseClient.client

    // Estado: Lista de billetes que el usuario puede comprar
    private val _availableTickets = MutableStateFlow(listOf(
        Ticket("1", "Billete Sencillo", "Viaje único en bus urbano", 1.30),
        Ticket("2", "Bono 10 Viajes", "Tarjeta multiviaje recargable", 8.40),
        Ticket("3", "Tarjeta Mensual", "Viajes ilimitados 30 días", 39.95)
    ))
    val availableTickets: StateFlow<List<Ticket>> = _availableTickets

    // Estado: Saldo actual del usuario (se observa desde la UI)
    private val _userBalance = MutableStateFlow(0.0)
    val userBalance: StateFlow<Double> = _userBalance

    init {
        // Al iniciar el ViewModel, intentamos cargar el saldo real
        cargarSaldoReal()
    }

    /**
     * Intenta conectar con Supabase para obtener el saldo guardado
     * en la tabla 'profiles' del usuario a crear
     */
    fun cargarSaldoReal() {
        viewModelScope.launch {
            try {
                // Obtenemos el usuario que ha iniciado sesión actualmente
                val user = supabase.auth.currentUserOrNull()
                if (user != null) {
                    // Preparamos la consulta a la tabla 'profiles' filtrando por el ID del usuario
                    val resultado = supabase.from("profiles")
                        .select {
                            filter { eq("id", user.id) }
                        }
                    
                    // TODO: Descomentar una vez que el modelo Profile esté integrado y la tabla creada
                    // _userBalance.value = resultado.decodeSingle<Profile>().balance
                    
                    // Mock: Ponemos un saldo por defecto para que la app sea funcional durante el desarrollo
                    _userBalance.value = 12.50 
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _userBalance.value = 0.0
            }
        }
    }

    /**
     * Lógica de COMPRA: Valida si el usuario tiene saldo y procesa la transacción.
     * Está preparado para actualizar el saldo en la base de datos de forma real.
     */
    fun buyTicket(ticket: Ticket) {
        viewModelScope.launch {
            // Validación local: ¿Tiene suficiente dinero?
            if (_userBalance.value >= ticket.price) {
                val nuevoSaldo = _userBalance.value - ticket.price
                
                try {
                    val user = supabase.auth.currentUserOrNull()
                    if (user != null) {
                        // AQUÍ se enviaría el nuevo saldo a Supabase para que se guarde permanentemente
                        /*
                        supabase.from("profiles").update(
                            { "balance" setter nuevoSaldo }
                        ) {
                            filter { eq("id", user.id) }
                        }
                        */
                        
                        // Actualizamos el estado local para que la UI se refresque al instante
                        _userBalance.value = nuevoSaldo
                        
                        // TODO sugerencia: Podrías insertar la compra en una tabla de 'historial_compras'
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}