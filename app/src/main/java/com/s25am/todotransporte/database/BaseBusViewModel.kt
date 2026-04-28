package com.s25am.todotransporte.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada
import com.s25am.todotransporte.database.data.RespuestaParada
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Esta clase contiene lo que es COMÚN a ambas pantallas:
 * Cargar líneas y cargar las paradas de una línea.
 */
open class BaseBusViewModel : ViewModel() {
    protected val supabase = SupabaseClient.client

    private val _lineas = MutableStateFlow<List<Linea>>(emptyList())
    val lineas: StateFlow<List<Linea>> = _lineas

    private var _selectedLinea = MutableStateFlow<Linea?>(null)
    var selectedLinea: StateFlow<Linea?> = _selectedLinea

    private val _paradas = MutableStateFlow<List<Parada>>(emptyList())
    val paradas: StateFlow<List<Parada>> = _paradas

    init {
        cargarLineas()
    }

    private fun cargarLineas() {
        viewModelScope.launch {
            try {
                val resultado = supabase.from("Linea")
                    .select { order("id", order = Order.ASCENDING) }
                    .decodeList<Linea>()
                _lineas.value = resultado
                if (resultado.isNotEmpty()) {
                    seleccionarLinea(resultado.first())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    open fun seleccionarLinea(linea: Linea) {
        _selectedLinea.value = linea
        cargarParadasDeLinea(linea.id)
    }

    protected fun cargarParadasDeLinea(lineaId: Int) {
        viewModelScope.launch {
            try {
                val cajas = supabase.from("Linea_Parada")
                    .select(Columns.Companion.raw("Parada(*)")) {
                        filter { eq("id_linea", lineaId) }
                    }
                    .decodeList<RespuestaParada>()
                _paradas.value = cajas.mapNotNull { it.parada }
                onParadasCargadas(lineaId)
            } catch (e: Exception) {
                e.printStackTrace()
                _paradas.value = emptyList()
            }
        }
    }

    // Función que los hijos pueden usar para hacer cosas extra cuando se cargan las paradas
    open fun onParadasCargadas(lineaId: Int) {}

    protected fun corregirHoraGtfs(horaGtfs: String): String {
        return try {
            val partes = horaGtfs.split(":")
            val horasCorregidas = partes[0].toInt() % 24
            "${horasCorregidas.toString().padStart(2, '0')}:${partes[1]}"
        } catch (e: Exception) {
            if (horaGtfs.length >= 5) horaGtfs.substring(0, 5) else horaGtfs
        }
    }
}
