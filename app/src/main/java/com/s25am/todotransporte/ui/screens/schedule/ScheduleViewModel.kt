package com.s25am.todotransporte.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.database.SupabaseClient
import com.s25am.todotransporte.database.data.Calendario
import com.s25am.todotransporte.database.data.Horario
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada
import com.s25am.todotransporte.database.data.RespuestaParada
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class ScheduleViewModel : ViewModel() {
    private val supabase = SupabaseClient.client

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()



    init {
        cargarLineas()
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

                _uiState.update { it.copy(lineas = resultado) }

                if (resultado.isNotEmpty()) {
                    seleccionarLinea(resultado.first())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /**
     * Función que se llama al seleccionar línea y resetear dirección.
     */
    fun seleccionarLinea(linea: Linea) {
        _uiState.update { it.copy(selectedLinea = linea, direccionActual = 0) }
        actualizarNombreDestino(linea.id, 0)
        cargarParadasDeLinea(linea.id, 0)
        actualizarProximosBuses(linea.id, 0)
    }


    /**
     * Función para alternar entre Ida y Vuelta.
     */
    fun alternarDireccion() {
        val currentState = _uiState.value
        val lineaActual = currentState.selectedLinea ?: return
        val nuevaDireccion = if (currentState.direccionActual == 0) 1 else 0

        _uiState.update { it.copy(direccionActual = nuevaDireccion) }

        actualizarNombreDestino(lineaActual.id, nuevaDireccion)
        cerrarDialogo()
        cargarParadasDeLinea(lineaActual.id, nuevaDireccion)
        actualizarProximosBuses(lineaActual.id, nuevaDireccion)
    }


    /**
     * Función interna para actualizar el nombre del destino
     */
    private fun actualizarNombreDestino(lineaId: Int, direccion: Int) {
        viewModelScope.launch {
            try {
                val resultado = supabase.from("Horario")
                    .select(Columns.list("destino")) {
                        filter {
                            eq("id_linea", lineaId)
                            eq("direccion", direccion)
                        }
                        limit(1)
                    }.decodeSingleOrNull<Horario>()

                _uiState.update { it.copy(destino = resultado?.destino ?: "Desconocido") }
            } catch (e: Exception) {
                _uiState.update { it.copy(destino = "Error al cargar destino") }
            }
        }
    }


    /**
     * Función que carga las paradas asociadas a una línea específica.
     */
    private fun cargarParadasDeLinea(lineaId: Int, direccion: Int) {
        viewModelScope.launch {
            try {
                val cajas = supabase.from("Linea_Parada")
                    .select(Columns.Companion.raw("Parada(*)")) {
                        filter {
                            eq("id_linea", lineaId)
                            eq("direccion", direccion)
                        }
                        order("orden", order = Order.ASCENDING)
                    }
                    .decodeList<RespuestaParada>()

                val paradasLimpio = cajas.mapNotNull { it.parada }
                _uiState.update { it.copy(paradas = paradasLimpio) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(paradas = emptyList()) }
            }
        }
    }


    /**
     * Función que actualiza la parada seleccionada y consulta sus horarios.
     */
    fun mostrarInfoParada(parada: Parada) {
        _uiState.update { it.copy(paradaSeleccionada = parada) }
        obtenerHorariosDeParada(parada.id)
    }


    /**
     * Función que según la parada seleccionada obtiene la lista de todos los horarios.
     */
    private fun obtenerHorariosDeParada(paradaId: Int) {
        val currentState = _uiState.value
        val lineaId = currentState.selectedLinea?.id ?: return
        val direccion = currentState.direccionActual

        viewModelScope.launch {
            try {
                val zonaEspanya = TimeZone.getTimeZone("Europe/Madrid")
                val formatoFecha = SimpleDateFormat("yyyyMMdd")
                formatoFecha.timeZone = zonaEspanya
                val fechaActualStr = formatoFecha.format(Calendar.getInstance().time)

                val calendario =
                    supabase
                        .from("Calendario")
                        .select(columns = Columns.list("service_id")) {
                            filter { eq("fecha", fechaActualStr) }
                        }
                        .decodeSingleOrNull<Calendario>()

                val serviceIdHoy = calendario?.service_id ?: return@launch

                val resultados = supabase.from("Horario")
                    .select {
                        filter {
                            eq("id_linea", lineaId)
                            eq("id_parada", paradaId)
                            eq("service_id", serviceIdHoy)
                            eq("direccion", direccion)
                        }
                        order("hora_llegada", order = Order.ASCENDING)
                    }.decodeList<Horario>()

                val listaLimpia = resultados
                    .map { it.copy(hora_llegada = corregirHoraGtfs(it.hora_llegada)) }
                    .distinctBy { it.hora_llegada }
                    .sortedBy { it.hora_llegada }

                _uiState.update { it.copy(horariosParada = listaLimpia) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(horariosParada = emptyList()) }
            }
        }
    }


    fun actualizarProximosBuses(lineaId: Int, direccion: Int) {
        viewModelScope.launch {
            try {
                val zonaEspanya = TimeZone.getTimeZone("Europe/Madrid")
                val formatoFecha = SimpleDateFormat("yyyyMMdd")
                val formatoHora = SimpleDateFormat("HH:mm:ss")
                formatoFecha.timeZone = zonaEspanya
                formatoHora.timeZone = zonaEspanya

                val fechaActualStr = formatoFecha.format(Calendar.getInstance().time)
                val horaActualStr = formatoHora.format(Calendar.getInstance().time)

                val calendario = supabase
                    .from("Calendario")
                    .select(columns = Columns.list("service_id")) {
                        filter { eq("fecha", fechaActualStr) }
                    }
                    .decodeSingleOrNull<Calendario>()

                val serviceIdHoy = calendario?.service_id ?: return@launch

                val resultados = supabase.from("Horario")
                    .select {
                        filter {
                            eq("id_linea", lineaId)
                            eq("service_id", serviceIdHoy)
                            eq("direccion", direccion)
                            gte("hora_llegada", horaActualStr)
                        }
                        order("hora_llegada", order = Order.ASCENDING)
                    }
                    .decodeList<Horario>()

                val proximos = resultados
                    .groupBy { it.id_parada }
                    .mapValues { entry ->
                        corregirHoraGtfs(entry.value.first().hora_llegada)
                    }

                _uiState.update { it.copy(proximosBusesParadas = proximos) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    /**
     * Función que corrige el formato de las horas GTFS para que se ajusten al rango de 24 horas.
     */
    private fun corregirHoraGtfs(horaGtfs: String): String {
        return try {
            val partes = horaGtfs.split(":")
            val horasCorregidas = partes[0].toInt() % 24
            "${horasCorregidas.toString().padStart(2, '0')}:${partes[1]}"
        } catch (e: Exception) {
            if (horaGtfs.length >= 5) horaGtfs.substring(0, 5) else horaGtfs
        }
    }


    /**
     * Limpia los estados de la parada seleccionada.
     */
    fun cerrarDialogo() {
        _uiState.update {
            it.copy(
                paradaSeleccionada = null,
                horariosParada = emptyList()
            )
        }
    }
}