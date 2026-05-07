package com.s25am.todotransporte.ui.screens.sale_point

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.database.SupabaseClient
import com.s25am.todotransporte.database.data.PuntoVenta
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SalePointViewModel : ViewModel() {
    private val supabase = SupabaseClient.client

    private val _uiState = MutableStateFlow(SalePointUiState())
    val uiState: StateFlow<SalePointUiState> = _uiState.asStateFlow()



    init {
        cargarPuntosVenta()
    }


    /**
     * Descarga todos los puntos de venta de Supabase de una vez.
     */
    private fun cargarPuntosVenta() {
        viewModelScope.launch {
            try {
                val resultados = supabase.from("Punto_Venta")
                    .select()
                    .decodeList<PuntoVenta>()

                _uiState.update { it.copy(puntosVenta = resultados) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(puntosVenta = emptyList()) }
            }
        }
    }


    /**
     * Guarda el punto que el usuario ha tocado en el mapa.
     */
    fun seleccionarPunto(punto: PuntoVenta) {
        _uiState.update { it.copy(puntoSeleccionado = punto) }
    }


    /**
     * Limpia la selección, lo que hará que se cierre el diálogo.
     */
    fun cerrarDialogo() {
        _uiState.update { it.copy(puntoSeleccionado = null) }
    }
}