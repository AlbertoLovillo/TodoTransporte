package com.s25am.todotransporte.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DataBaseViewModel : ViewModel() {
    private val supabase = SupabaseClient.client

    private val _lineas = MutableStateFlow<List<Linea>>(emptyList())
    val lineas: StateFlow<List<Linea>> = _lineas

    private var _selectedLinea = MutableStateFlow<Linea?>(null)
    var selectedLinea: StateFlow<Linea?> = _selectedLinea


    init {
        cargarLineas()
    }

    private fun cargarLineas() {
        viewModelScope.launch {
            try {
                val resultado = supabase.from("Linea")
                    .select {
                        order("id", order = Order.ASCENDING)
                    }
                    .decodeList<Linea>()

                _lineas.value = resultado

                if (resultado.isNotEmpty()) {
                    _selectedLinea.value = resultado.first()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun seleccionarLinea(linea: Linea) {
        _selectedLinea.value = linea
        cargarParadasDeLinea(linea.id)
    }



    private val _paradas = MutableStateFlow<List<Parada>>(emptyList())
    val paradas: StateFlow<List<Parada>> = _paradas

    private val _paradaSeleccionada = MutableStateFlow<Parada?>(null)
    val paradaSeleccionada: StateFlow<Parada?> = _paradaSeleccionada

    private val _proximoBusHora = MutableStateFlow<String?>(null)
    val proximoBusHora: StateFlow<String?> = _proximoBusHora


    private fun cargarParadasDeLinea(lineaId: Int) {
        viewModelScope.launch {
            try {
                val cajas = supabase.from("Linea_Parada")
                    .select(Columns.Companion.raw("Parada(*)")) {
                        filter { eq("id_linea", lineaId) }
                    }
                    .decodeList<RespuestaParada>()

                val resultadoLimpio = cajas.mapNotNull { it.parada }

                _paradas.value = resultadoLimpio
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun mostrarInfoParada(parada: Parada) {
        _paradaSeleccionada.value = parada
        obtenerHorario(parada.id)
    }


    private fun obtenerHorario(paradaId: Int) {
        val lineaId = selectedLinea.value?.id ?: return

        viewModelScope.launch {
            try {
                _proximoBusHora.value = "Buscando..."

                val zonaEspanya = ZoneId.of("Europe/Madrid")
                val horaActual = LocalTime.now(zonaEspanya)
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                val horaActualStr = horaActual.format(formatter)

                val resultados = supabase.from("Horario")
                    .select(columns = Columns.Companion.list("hora_llegada")) {
                        filter {
                            eq("id_linea", lineaId)
                            eq("id_parada", paradaId)
                            gte("hora_llegada", horaActualStr)
                        }
                        order("hora_llegada", order = Order.ASCENDING)
                        limit(1)
                    }.decodeList<Horario>()

                if (resultados.isNotEmpty()) {
                    val proximaHoraBruta = resultados.first().hora_llegada

                    _proximoBusHora.value = corregirHoraGtfs(proximaHoraBruta)
                } else {
                    _proximoBusHora.value = "No hay más buses hasta las 6:00"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _proximoBusHora.value = "Error al consultar"
            }
        }
    }


    private fun corregirHoraGtfs(horaGtfs: String): String {
        try {
            val partes = horaGtfs.split(":")
            val horasOriginales = partes[0].toInt()
            val minutos = partes[1]

            val horasCorregidas = horasOriginales % 24

            val horasStr = horasCorregidas.toString().padStart(2, '0')

            return "$horasStr:$minutos"
        } catch (e: Exception) {
            return if (horaGtfs.length >= 5) horaGtfs.substring(0, 5) else horaGtfs
        }
    }


    fun cerrarDialogo() {
        _paradaSeleccionada.value = null
        _proximoBusHora.value = null
    }
}