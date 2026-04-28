package com.s25am.todotransporte.ui.screens.maps

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

class MapsViewModel : BaseBusViewModel() {

    private val _paradaSeleccionada = MutableStateFlow<Parada?>(null)
    val paradaSeleccionada: StateFlow<Parada?> = _paradaSeleccionada

    private val _proximoBusHora = MutableStateFlow<String?>(null)
    val proximoBusHora: StateFlow<String?> = _proximoBusHora

    private val _horariosParada = MutableStateFlow<List<Horario>>(emptyList())
    val horariosParada: StateFlow<List<Horario>> = _horariosParada

    fun mostrarInfoParada(parada: Parada) {
        _paradaSeleccionada.value = parada
        obtenerHorario(parada.id)
        obtenerHorariosDeParada(parada.id)
    }

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

                val resultados = supabase.from("Horario")
                    .select {
                        filter {
                            eq("id_linea", lineaId)
                            eq("id_parada", paradaId)
                            eq("service_id", serviceIdHoy)
                        }
                        order("hora_llegada", order = Order.ASCENDING)
                    }.decodeList<Horario>()

                _horariosParada.value = resultados
                    .map { it.copy(hora_llegada = corregirHoraGtfs(it.hora_llegada)) }
                    .distinctBy { it.hora_llegada }
                    .sortedBy { it.hora_llegada }
            } catch (e: Exception) {
                e.printStackTrace()
                _horariosParada.value = emptyList()
            }
        }
    }

    /*
    fun obtenerHorariosDeParada(paradaId: Int) {
        val lineaId = selectedLinea.value?.id ?: return

        viewModelScope.launch {
            try {
                val resultados = supabase.from("Horario")
                    .select {
                        filter {
                            eq("id_linea", lineaId)
                            eq("id_parada", paradaId)
                        }
                        order("hora_llegada", order = Order.ASCENDING)
                    }.decodeList<Horario>()

                _horariosParada.value = resultados.map { it.copy(hora_llegada = corregirHoraGtfs(it.hora_llegada)) }
            } catch (e: Exception) {
                e.printStackTrace()
                _horariosParada.value = emptyList()
            }
        }
    }
    */

    private fun obtenerHorario(paradaId: Int) {
        val lineaId = selectedLinea.value?.id ?: return

        viewModelScope.launch {
            try {
                _proximoBusHora.value = "Calculando..."

                // 1. Averiguar QUÉ DÍA ES HOY (Formato 20260428)
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

                // Si no hay bus hoy (o hay un error), abortamos
                val serviceIdHoy = calendario?.service_id

                if (serviceIdHoy == null) {
                    _proximoBusHora.value = "No hay servicio hoy"
                    return@launch
                }

                // 3. BUSCAR EL HORARIO (Añadimos el filtro mágico eq("service_id", serviceIdHoy))
                val resultados =
                    supabase.from("Horario").select {
                        filter {
                            eq("id_linea", lineaId)
                            eq("id_parada", paradaId)
                            eq("service_id", serviceIdHoy)
                            gte("hora_llegada", horaActualStr)
                        }
                        order("hora_llegada", order = Order.ASCENDING)
                        limit(1)
                    }.decodeList<Horario>()

                // 4. Mostrar el resultado
                if (resultados.isNotEmpty()) {
                    val proximaHoraBruta = resultados.first().hora_llegada
                    _proximoBusHora.value = corregirHoraGtfs(proximaHoraBruta)
                } else {
                    _proximoBusHora.value = "No hay más rutas hoy"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _proximoBusHora.value = "Error al consultar"
            }
        }
    }

//    private fun obtenerHorario(paradaId: Int) {
//        val lineaId = selectedLinea.value?.id ?: return
//
//        viewModelScope.launch {
//            try {
//                _proximoBusHora.value = "Buscando..."
//
//                val zonaEspanya = ZoneId.of("Europe/Madrid")
//                val horaActual = LocalTime.now(zonaEspanya)
//                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
//                val horaActualStr = horaActual.format(formatter)
//
//                val resultados = supabase.from("Horario")
//                    .select {
//                        filter {
//                            eq("id_linea", lineaId)
//                            eq("id_parada", paradaId)
//                            gte("hora_llegada", horaActualStr)
//                        }
//                        order("hora_llegada", order = Order.ASCENDING)
//                        limit(1)
//                    }.decodeList<Horario>()
//
//                if (resultados.isNotEmpty()) {
//                    val proximaHoraBruta = resultados.first().hora_llegada
//                    _proximoBusHora.value = corregirHoraGtfs(proximaHoraBruta)
//                } else {
//                    _proximoBusHora.value = "No hay más buses hoy"
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _proximoBusHora.value = "Error al consultar"
//            }
//        }
//    }

    fun cerrarDialogo() {
        _paradaSeleccionada.value = null
        _proximoBusHora.value = null
        _horariosParada.value = emptyList()
    }
}
