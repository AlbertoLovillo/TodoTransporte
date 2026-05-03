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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class ScheduleViewModel : ViewModel() {
    private val supabase = SupabaseClient.client

    private val _lineas = MutableStateFlow<List<Linea>>(emptyList())
    val lineas: StateFlow<List<Linea>> = _lineas

    private var _selectedLinea = MutableStateFlow<Linea?>(null)
    var selectedLinea: StateFlow<Linea?> = _selectedLinea

    private val _paradas = MutableStateFlow<List<Parada>>(emptyList())
    val paradas: StateFlow<List<Parada>> = _paradas


    private val _paradaSeleccionada = MutableStateFlow<Parada?>(null)
    val paradaSeleccionada: StateFlow<Parada?> = _paradaSeleccionada

    private val _proximoBusHora = MutableStateFlow<String?>(null)
    val proximoBusHora: StateFlow<String?> = _proximoBusHora

    private val _horariosParada = MutableStateFlow<List<Horario>>(emptyList())
    val horariosParada: StateFlow<List<Horario>> = _horariosParada

    private val _proximosBusesParadas = MutableStateFlow<Map<Int, String>>(emptyMap())
    val proximosBusesParadas: StateFlow<Map<Int, String>> = _proximosBusesParadas

    private val _direccionActual = MutableStateFlow<Int>(0)
    val direccionActual: StateFlow<Int> = _direccionActual

    private val _destino = MutableStateFlow<String?>(null)
    val destino: StateFlow<String?> = _destino


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

                _lineas.value = resultado

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
        _selectedLinea.value = linea
        _direccionActual.value = 0
        actualizarNombreDestino(linea.id, 0)
        cargarParadasDeLinea(linea.id, 0)
        actualizarProximosBuses(linea.id, 0)
    }


    /**
     * NUEVO: Función para alternar entre Ida y Vuelta.
     */
    fun alternarDireccion() {
        val lineaActual = _selectedLinea.value ?: return
        val nuevaDireccion = if (_direccionActual.value == 0) 1 else 0
        _direccionActual.value = nuevaDireccion
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

                _destino.value = resultado?.destino ?: "Desconocido"
            } catch (e: Exception) {
                _destino.value = "Error al cargar destino"
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

                _paradas.value = cajas.mapNotNull { it.parada }
            } catch (e: Exception) {
                e.printStackTrace()
                _paradas.value = emptyList()
            }
        }
    }


    /**
     * Función que actualiza la parada seleccionada y consulta sus horarios.
     */
    fun mostrarInfoParada(parada: Parada) {
        _paradaSeleccionada.value = parada
        obtenerHorario(parada.id)
        obtenerHorariosDeParada(parada.id)
    }


    /**
     * Función que según la parada seleccionada obtiene la lista de todos los horarios.
     */
    private fun obtenerHorariosDeParada(paradaId: Int) {
        val lineaId = selectedLinea.value?.id ?: return
        val direccion = _direccionActual.value

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
                            eq("direccion", direccion) // Filtro mágico
                        }
                        order("hora_llegada", order = Order.ASCENDING)
                    }.decodeList<Horario>()

                val listaLimpia = resultados
                    .map { it.copy(hora_llegada = corregirHoraGtfs(it.hora_llegada)) }
                    .distinctBy { it.hora_llegada }
                    .sortedBy { it.hora_llegada }

                _horariosParada.value = listaLimpia
            } catch (e: Exception) {
                e.printStackTrace()
                _horariosParada.value = emptyList()
            }
        }
    }


    /**
     * Función que busca el próximo autobús que pasará por la parada según la hora actual.
     */
    private fun obtenerHorario(paradaId: Int) {
        val lineaId = selectedLinea.value?.id ?: return
        val direccion = _direccionActual.value

        viewModelScope.launch {
            try {
                _proximoBusHora.value = "Calculando..."

                val zonaEspanya = TimeZone.getTimeZone("Europe/Madrid")
                val formatoFecha = SimpleDateFormat("yyyyMMdd")
                formatoFecha.timeZone = zonaEspanya
                val formatoHora = SimpleDateFormat("HH:mm:ss")
                formatoHora.timeZone = zonaEspanya
                val fechaActualStr = formatoFecha.format(Calendar.getInstance().time)
                val horaActualStr = formatoHora.format(Calendar.getInstance().time)

                val calendario =
                    supabase.from("Calendario").select(columns = Columns.list("service_id")) {
                        filter { eq("fecha", fechaActualStr) }
                    }.decodeSingleOrNull<Calendario>()
                val serviceIdHoy = calendario?.service_id ?: return@launch

                val resultados = supabase.from("Horario").select {
                    filter {
                        eq("id_linea", lineaId)
                        eq("id_parada", paradaId)
                        eq("service_id", serviceIdHoy)
                        eq("direccion", direccion) // Filtro mágico
                        gte("hora_llegada", horaActualStr)
                    }
                    order("hora_llegada", order = Order.ASCENDING)
                    limit(1)
                }.decodeList<Horario>()

                if (resultados.isNotEmpty()) {
                    val primerBus = resultados.first()
                    val proximaHoraLimpia = corregirHoraGtfs(primerBus.hora_llegada)
                    val destinoInfo = primerBus.destino?.let { " a $it" } ?: ""
                    _proximoBusHora.value = "$proximaHoraLimpia$destinoInfo"
                } else {
                    _proximoBusHora.value = "No hay más rutas hoy"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _proximoBusHora.value = "Error al consultar"
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

                _proximosBusesParadas.value = proximos
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

//    private fun corregirHoraGtfs(horaGtfs: String): String {
//        try {
//            val partes = horaGtfs.split(":")
//            val horasOriginales = partes[0].toInt()
//            val minutos = partes[1]
//            val horasCorregidas = horasOriginales % 24
//            val horasStr = horasCorregidas.toString().padStart(2, '0')
//            return "$horasStr:$minutos"
//        } catch (e: Exception) {
//            return if (horaGtfs.length >= 5) horaGtfs.substring(0, 5) else horaGtfs
//        }
//    }


    /**
     * Limpia los estados de la parada seleccionada.
     */
    fun cerrarDialogo() {
        _paradaSeleccionada.value = null
        _proximoBusHora.value = null
        _horariosParada.value = emptyList()
    }
}
