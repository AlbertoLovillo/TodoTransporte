package com.s25am.todotransporte.ui.screens.sale_point

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.database.SupabaseClient
import com.s25am.todotransporte.database.data.PuntoVenta
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SalePointViewModel : ViewModel() {
    private val supabase = SupabaseClient.client

    private val _puntosVenta = MutableStateFlow<List<PuntoVenta>>(emptyList())
    val puntosVenta: StateFlow<List<PuntoVenta>> = _puntosVenta

    private val _puntoSeleccionado = MutableStateFlow<PuntoVenta?>(null)
    val puntoSeleccionado: StateFlow<PuntoVenta?> = _puntoSeleccionado



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

                _puntosVenta.value = resultados
            } catch (e: Exception) {
                e.printStackTrace()
                _puntosVenta.value = emptyList()
            }
        }
    }


    /**
     * Guarda el punto que el usuario ha tocado en el mapa.
     */
    fun seleccionarPunto(punto: PuntoVenta) {
        _puntoSeleccionado.value = punto
    }


    /**
     * Limpia la selección, lo que hará que se cierre el diálogo.
     */
    fun cerrarDialogo() {
        _puntoSeleccionado.value = null
    }
}