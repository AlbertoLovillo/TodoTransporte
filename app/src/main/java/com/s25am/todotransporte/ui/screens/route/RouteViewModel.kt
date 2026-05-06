package com.s25am.todotransporte.ui.screens.route

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel para la planificación de rutas.
 */
class RouteViewModel : ViewModel() {
    
    // Estado para el punto de origen
    private val _origen = MutableStateFlow("")
    val origen: StateFlow<String> = _origen

    // Estado para el punto de destino
    private val _destino = MutableStateFlow("")
    val destino: StateFlow<String> = _destino

    // Mock: Lista de billetes recientes guardados para uso offline
    private val _recentTickets = MutableStateFlow(listOf("Billete Sencillo", "Bono 10 Viajes"))
    val recentTickets: StateFlow<List<String>> = _recentTickets

    fun updateOrigen(nuevoOrigen: String) {
        _origen.value = nuevoOrigen
    }

    fun updateDestino(nuevoDestino: String) {
        _destino.value = nuevoDestino
    }

    /**
     * Simulación de búsqueda de ruta.
     */
    fun buscarRuta() {
        if (_origen.value.isNotBlank() && _destino.value.isNotBlank()) {
            // TODO: Integrar con una API de routing (Google Maps, OpenRouteService, etc.)
            println("Buscando ruta desde ${_origen.value} hasta ${_destino.value}")
        }
    }
}
