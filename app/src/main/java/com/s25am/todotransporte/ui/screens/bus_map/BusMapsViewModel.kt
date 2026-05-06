package com.s25am.todotransporte.ui.screens.bus_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.database.SupabaseClient
import com.s25am.todotransporte.database.data.Calendario
import com.s25am.todotransporte.database.data.Horario
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada
import com.s25am.todotransporte.database.data.RespuestaParada
import com.s25am.todotransporte.database.data.RutaGeometria
import com.s25am.todotransporte.database.data.BusPosition
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class BusMapsViewModel : ViewModel() {
    private val supabase = SupabaseClient.client
    private val httpClient = HttpClient()

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

    private val _direccionActual = MutableStateFlow<Int>(0)
    val direccionActual: StateFlow<Int> = _direccionActual

    private val _rutaGeojsonActual = MutableStateFlow<String?>(null)
    val rutaGeojsonActual: StateFlow<String?> = _rutaGeojsonActual

    private val _destino = MutableStateFlow<String?>(null)
    val destino: StateFlow<String?> = _destino

    private val _busesEnTiempoReal = MutableStateFlow<List<BusPosition>>(emptyList()) // Tiempo real: Almacena la ubicación de los buses
    val busesEnTiempoReal: StateFlow<List<BusPosition>> = _busesEnTiempoReal

    init {
        cargarLineas()
        iniciarSeguimientoBuses()
    }

    /**
     * Tiempo real: Inicia un bucle que descarga la ubicación de los buses cada minuto
     */
    private fun iniciarSeguimientoBuses() {
        viewModelScope.launch {
            while (true) {
                actualizarPosicionesBuses()
                delay(60000) // 1 minuto
            }
        }
    }


    /**
     * Tiempo real- Descarga el CSV de OpenData Málaga y filtra los buses por línea y sentido
     */
    private suspend fun actualizarPosicionesBuses() {
        try {
            val url = "https://datosabiertos.malaga.eu/recursos/transporte/EMT/EMTlineasUbicaciones/lineasyubicaciones.csv"
            val response = httpClient.get(url)
            val csvText = response.bodyAsText()
            
            val lineasCsv = csvText.lines().drop(1) // Quitamos la cabecera
            val todosLosBuses = lineasCsv.mapNotNull { linea ->
                val datos = linea.replace("\"", "").split(",")
                if (datos.size >= 7) {
                    BusPosition(
                        codBus = datos[0],
                        codLinea = datos[1],
                        sentido = datos[2].toIntOrNull() ?: 1,
                        lon = datos[3].toDoubleOrNull() ?: 0.0,
                        lat = datos[4].toDoubleOrNull() ?: 0.0,
                        lastUpdate = datos[6]
                    )
                } else null
            }

            // Normalización para comparar: "41" -> "41.0"
            val codigoLineaSeleccionada = _selectedLinea.value?.codigo ?: ""
            val codigoNormalizado = if (codigoLineaSeleccionada.toDoubleOrNull() != null) {
                codigoLineaSeleccionada.toDouble().toString()
            } else {
                codigoLineaSeleccionada
            }
            
            _busesEnTiempoReal.value = todosLosBuses.filter { 
                it.codLinea == codigoNormalizado && it.sentido == (_direccionActual.value + 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
     * Función que según la línea seleccionada actualiza la lista de paradas y resetea el sentido.
     */
    fun seleccionarLinea(linea: Linea) {
        _selectedLinea.value = linea
        _direccionActual.value = 0
        actualizarNombreDestino(linea.id, 0)
        cerrarDialogo()
        cargarDatosPorSentido(linea.id, 0)
        viewModelScope.launch { actualizarPosicionesBuses() }
    }


    /**
     * Función para cambiar entre Ida y Vuelta.
     */
    fun alternarDireccion() {
        val lineaActual = _selectedLinea.value ?: return
        val nuevaDireccion = if (_direccionActual.value == 0) 1 else 0
        _direccionActual.value = nuevaDireccion
        actualizarNombreDestino(lineaActual.id, nuevaDireccion)

        cerrarDialogo()
        cargarDatosPorSentido(lineaActual.id, nuevaDireccion)
        viewModelScope.launch { actualizarPosicionesBuses() }
    }


    /**
     * Función que carga las paradas (ordenadas) y el dibujo (GeoJSON) según el sentido.
     */
    private fun cargarDatosPorSentido(lineaId: Int, direccion: Int) {
        viewModelScope.launch {
            // 1. CARGAMOS LAS PARADAS
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

            try {
                val ruta = supabase.from("Ruta_Geometria")
                    .select {
                        filter {
                            eq("id_linea", lineaId)
                            eq("direccion", direccion)
                        }
                    }.decodeSingleOrNull<RutaGeometria>()

                _rutaGeojsonActual.value = ruta?.geojson
            } catch (e: Exception) {
                e.printStackTrace()
                _rutaGeojsonActual.value = null
            }
        }
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
    private fun cargarParadasDeLinea(lineaId: Int) {
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
    private fun obtenerHorariosDeParada(paradaId: Int) {
        val lineaId = selectedLinea.value?.id ?: return

        viewModelScope.launch {
            try {
                val zonaEspanya = TimeZone.getTimeZone("Europe/Madrid")
                val formatoFecha = SimpleDateFormat("yyyyMMdd")
                formatoFecha.timeZone = zonaEspanya
                val fechaActualStr = formatoFecha.format(Calendar.getInstance().time)

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
                            eq("direccion", _direccionActual.value)
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


    /**
     * Función que busca el próximo autobús que pasará por la parada según la hora actual.
     */
    private fun obtenerHorario(paradaId: Int) {
        val lineaId = selectedLinea.value?.id ?: return

        viewModelScope.launch {
            try {
                _proximoBusHora.value = "Calculando..."

                val zonaEspanya = TimeZone.getTimeZone("Europe/Madrid")
                val formatoFecha = SimpleDateFormat("yyyyMMdd")
                formatoFecha.timeZone = zonaEspanya
                val fechaActualStr = formatoFecha.format(Calendar.getInstance().time)

                val formatoHora = SimpleDateFormat("HH:mm:ss")
                formatoHora.timeZone = zonaEspanya
                val horaActualStr = formatoHora.format(Calendar.getInstance().time)

                val calendario =
                    supabase.from("Calendario").select(columns = Columns.list("service_id")) {
                        filter { eq("fecha", fechaActualStr) }
                    }.decodeSingleOrNull<Calendario>()

                val serviceIdHoy = calendario?.service_id

                if (serviceIdHoy == null) {
                    _proximoBusHora.value = "No hay servicio hoy"
                    return@launch
                }

                val resultados =
                    supabase.from("Horario").select {
                        filter {
                            eq("id_linea", lineaId)
                            eq("id_parada", paradaId)
                            eq("service_id", serviceIdHoy)
                            eq("direccion", _direccionActual.value)
                            gte("hora_llegada", horaActualStr)
                        }
                        order("hora_llegada", order = Order.ASCENDING)
                        limit(1)
                    }.decodeList<Horario>()

                if (resultados.isNotEmpty()) {
                    val primerBus = resultados.first()
                    val proximaHoraLimpia = corregirHoraGtfs(primerBus.hora_llegada)

                    val destino = primerBus.destino ?: "Fin de trayecto"
                    _proximoBusHora.value = "$proximaHoraLimpia hacia $destino"
                } else {
                    _proximoBusHora.value = "No hay más buses en este sentido"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _proximoBusHora.value = "Error al consultar"
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