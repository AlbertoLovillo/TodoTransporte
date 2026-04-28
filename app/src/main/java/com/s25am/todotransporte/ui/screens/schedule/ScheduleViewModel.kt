package com.s25am.todotransporte.ui.screens.schedule

import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.database.BaseBusViewModel
import com.s25am.todotransporte.database.data.Calendario
import com.s25am.todotransporte.database.data.Horario
import com.s25am.todotransporte.database.data.Parada
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class ScheduleViewModel : BaseBusViewModel() {

    private val _proximosBusesParadas = MutableStateFlow<Map<Int, String>>(emptyMap())
    val proximosBusesParadas: StateFlow<Map<Int, String>> = _proximosBusesParadas

    private val _paradaSeleccionada = MutableStateFlow<Parada?>(null)
    val paradaSeleccionada: StateFlow<Parada?> = _paradaSeleccionada

    private val _horariosParada = MutableStateFlow<List<Horario>>(emptyList())
    val horariosParada: StateFlow<List<Horario>> = _horariosParada

    override fun onParadasCargadas(lineaId: Int) {
        actualizarProximosBuses(lineaId)
    }

    fun mostrarInfoParada(parada: Parada) {
        _paradaSeleccionada.value = parada
        obtenerHorariosDeParada(parada.id)
    }

    fun cerrarDialogo() {
        _paradaSeleccionada.value = null
        _horariosParada.value = emptyList()
    }

    private fun obtenerHorariosDeParada(paradaId: Int) {
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

    private fun actualizarProximosBuses(lineaId: Int) {
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
}
