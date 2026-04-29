package com.s25am.todotransporte.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


    private val _paradaSeleccionada = MutableStateFlow<Parada?>(null)
    val paradaSeleccionada: StateFlow<Parada?> = _paradaSeleccionada

    private val _proximoBusHora = MutableStateFlow<String?>(null)
    val proximoBusHora: StateFlow<String?> = _proximoBusHora

    private val _horariosParada = MutableStateFlow<List<Horario>>(emptyList())
    val horariosParada: StateFlow<List<Horario>> = _horariosParada



    /**
     * Función que busca el próximo autobús que pasará por la parada según la hora actual.
     */
    fun obtenerHorario(paradaId: Int) {
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
                val serviceIdHoy = calendario?.service_id ?: return@launch

                // 3. BUSCAR EL HORARIO (Añadimos el filtro mágico eq("service_id", serviceIdHoy))
                val resultados =
                    supabase.from("Horario").select(columns = Columns.list("hora_llegada")) {
                        filter {
                            eq("id_linea", lineaId)
                            eq("id_parada", paradaId)
                            eq("service_id", serviceIdHoy) // <-- EL FILTRO SALVAVIDAS
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

}
