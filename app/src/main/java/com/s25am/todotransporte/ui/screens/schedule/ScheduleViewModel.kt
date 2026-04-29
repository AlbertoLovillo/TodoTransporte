package com.s25am.todotransporte.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.database.BaseBusViewModel
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
     * Función que según la línea seleccionada actualiza la lista de paradas.
     */
    open fun seleccionarLinea(linea: Linea) {
        _selectedLinea.value = linea
        cargarParadasDeLinea(linea.id)
    }


    /**
     * Función que carga las paradas asociadas a una línea específica.
     */
    protected fun cargarParadasDeLinea(lineaId: Int) {
        viewModelScope.launch {
            try {
                val cajas = supabase.from("Linea_Parada")
                    .select(Columns.Companion.raw("Parada(*)")) {
                        filter { eq("id_linea", lineaId) }
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
    fun obtenerHorariosDeParada(paradaId: Int) {
        val lineaId = selectedLinea.value?.id ?: return

        viewModelScope.launch {
            try {
                // 1. Averiguar QUÉ DÍA ES HOY
                val zonaEspaña = TimeZone.getTimeZone("Europe/Madrid")
                val formatoFecha = SimpleDateFormat("yyyyMMdd")
                formatoFecha.timeZone = zonaEspaña
                val fechaActualStr = formatoFecha.format(Calendar.getInstance().time)

                // 2. BUSCAR EN EL CALENDARIO EL CÓDIGO DE HOY
                val calendario =
                    supabase.from("Calendario").select(columns = Columns.list("service_id")) {
                        filter { eq("fecha", fechaActualStr) }
                    }.decodeSingleOrNull<Calendario>()

                val serviceIdHoy = calendario?.service_id ?: return@launch

                // Obtenemos los horarios filtrando por línea, parada y el service_id de hoy
                val resultados = supabase.from("Horario")
                    .select {
                        filter {
                            eq("id_linea", lineaId)
                            eq("id_parada", paradaId)
                            eq("service_id", serviceIdHoy)
                        }
                        order("hora_llegada", order = Order.ASCENDING)
                    }.decodeList<Horario>()

                // Limpiamos los resultados
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

    /*
    private fun obtenerHorariosDeParada(paradaId: Int) {
        val lineaId = selectedLinea.value?.id ?: return

        viewModelScope.launch {
            try {
                // Obtenemos los horarios filtrando por línea y parada
                val resultados = supabase.from("Horario")
                    .select {
                        filter {
                            eq("id_linea", lineaId)
                            eq("id_parada", paradaId)
                        }
                        order("hora_llegada", order = Order.ASCENDING)
                    }.decodeList<Horario>()

                // Limpiamos los resultados:
                // 1. Corregimos formato GTFS (ej. 25:00 -> 01:00)
                // 2. Tomamos solo las horas únicas (distintas)
                // 3. Volvemos a ordenar porque al corregir GTFS el orden original puede variar visualmente
                // 4. Tomamos solo los próximos horarios razonables para no saturar (opcional)
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
    */

    fun actualizarProximosBuses(lineaId: Int) {
        viewModelScope.launch {
            try {
                // 1. Averiguar QUÉ DÍA ES HOY
                val zonaEspaña = TimeZone.getTimeZone("Europe/Madrid")
                val formatoFecha = SimpleDateFormat("yyyyMMdd")
                formatoFecha.timeZone = zonaEspaña
                val fechaActualStr = formatoFecha.format(Calendar.getInstance().time)

                // Y LA HORA ACTUAL
                val formatoHora = SimpleDateFormat("HH:mm:ss")
                formatoHora.timeZone = zonaEspaña
                val horaActualStr = formatoHora.format(Calendar.getInstance().time)

                // 2. BUSCAR EN EL CALENDARIO EL CÓDIGO DE HOY
                val calendario =
                    supabase.from("Calendario").select(columns = Columns.list("service_id")) {
                        filter { eq("fecha", fechaActualStr) }
                    }.decodeSingleOrNull<Calendario>()

                val serviceIdHoy = calendario?.service_id ?: return@launch

                // 3. BUSCAR LOS HORARIOS
                val resultados = supabase.from("Horario")
                    .select {
                        filter {
                            eq("id_linea", lineaId)
                            eq("service_id", serviceIdHoy)
                            gte("hora_llegada", horaActualStr)
                        }
                        order("hora_llegada", order = Order.ASCENDING)
                    }.decodeList<Horario>()

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

    /*
    private fun actualizarProximosBuses(lineaId: Int) {
        viewModelScope.launch {
            try {
                val zonaEspanya = ZoneId.of("Europe/Madrid")
                val horaActual = LocalTime.now(zonaEspanya)
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                val horaActualStr = horaActual.format(formatter)

                val resultados = supabase.from("Horario")
                    .select {
                        filter {
                            eq("id_linea", lineaId)
                            gte("hora_llegada", horaActualStr)
                        }
                        order("hora_llegada", order = Order.ASCENDING)
                    }.decodeList<Horario>()

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
    */


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
