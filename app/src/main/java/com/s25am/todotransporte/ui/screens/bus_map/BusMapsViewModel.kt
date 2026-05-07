package com.s25am.todotransporte.ui.screens.bus_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s25am.todotransporte.database.SupabaseClient
import com.s25am.todotransporte.database.data.BusPosition
import com.s25am.todotransporte.database.data.Calendario
import com.s25am.todotransporte.database.data.Horario
import com.s25am.todotransporte.database.data.Linea
import com.s25am.todotransporte.database.data.Parada
import com.s25am.todotransporte.database.data.RespuestaParada
import com.s25am.todotransporte.database.data.RutaGeometria
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class BusMapsViewModel : ViewModel() {
    private val supabase = SupabaseClient.client
    private val httpClient = HttpClient()

    // Un único estado para toda la pantalla
    private val _uiState = MutableStateFlow(BusMapsUiState())
    val uiState: StateFlow<BusMapsUiState> = _uiState.asStateFlow()


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
            val url =
                "https://datosabiertos.malaga.eu/recursos/transporte/EMT/EMTlineasUbicaciones/lineasyubicaciones.csv"
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

            val currentState = _uiState.value
            val codigoLineaSeleccionada = currentState.selectedLinea?.codigo ?: ""

            _uiState.update { state ->
                state.copy(
                    busesEnTiempoReal = todosLosBuses.filter {
                        it.codLinea == codigoLineaSeleccionada && it.sentido == (state.direccionActual + 1)
                    }
                )
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
     * Función que según la línea seleccionada actualiza la lista de paradas y resetea el sentido.
     */
    fun seleccionarLinea(linea: Linea) {
        _uiState.update { it.copy(selectedLinea = linea, direccionActual = 0) }
        actualizarNombreDestino(linea.id, 0)
        cerrarDialogo()
        cargarDatosPorSentido(linea.id, 0)
        viewModelScope.launch { actualizarPosicionesBuses() }
    }


    /**
     * Función para cambiar entre Ida y Vuelta.
     */
    fun alternarDireccion() {
        val currentState = _uiState.value
        val lineaActual = currentState.selectedLinea ?: return
        val nuevaDireccion = if (currentState.direccionActual == 0) 1 else 0

        _uiState.update { it.copy(direccionActual = nuevaDireccion) }

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

                val paradasNuevas = cajas.mapNotNull { it.parada }
                _uiState.update { it.copy(paradas = paradasNuevas) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(paradas = emptyList()) }
            }

            try {
                val ruta = supabase.from("Ruta_Geometria")
                    .select {
                        filter {
                            eq("id_linea", lineaId)
                            eq("direccion", direccion)
                        }
                    }.decodeSingleOrNull<RutaGeometria>()

                _uiState.update { it.copy(rutaGeojsonActual = ruta?.geojson) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(rutaGeojsonActual = null) }
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

                _uiState.update { it.copy(destino = resultado?.destino ?: "Desconocido") }
            } catch (e: Exception) {
                _uiState.update { it.copy(destino = "Error al cargar destino") }
            }
        }
    }


    /**
     * Función que actualiza la parada seleccionada y consulta sus horarios.
     */
    fun mostrarInfoParada(parada: Parada) {
        _uiState.update { it.copy(paradaSeleccionada = parada) }
        obtenerHorario(parada.id)
    }


    /**
     * Función que busca el próximo autobús que pasará por la parada según la hora actual.
     */
    private fun obtenerHorario(paradaId: Int) {
        val currentState = _uiState.value
        val lineaId = currentState.selectedLinea?.id ?: return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(proximoBusHora = "Calculando...") }

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
                    _uiState.update { it.copy(proximoBusHora = "No hay servicio hoy") }
                    return@launch
                }

                val resultados =
                    supabase.from("Horario").select {
                        filter {
                            eq("id_linea", lineaId)
                            eq("id_parada", paradaId)
                            eq("service_id", serviceIdHoy)
                            eq("direccion", currentState.direccionActual)
                            gte("hora_llegada", horaActualStr)
                        }
                        order("hora_llegada", order = Order.ASCENDING)
                        limit(1)
                    }.decodeList<Horario>()

                if (resultados.isNotEmpty()) {
                    val primerBus = resultados.first()
                    val proximaHoraLimpia = corregirHoraGtfs(primerBus.hora_llegada)
                    val destino = primerBus.destino ?: "Fin de trayecto"

                    _uiState.update { it.copy(proximoBusHora = "$proximaHoraLimpia hacia $destino") }
                } else {
                    _uiState.update { it.copy(proximoBusHora = "No hay más buses en este sentido") }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(proximoBusHora = "Error al consultar") }
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
                proximoBusHora = null,
                horariosParada = emptyList()
            )
        }
    }

    fun NombreLinea(linea: Linea) {
        _uiState.update {
            it.copy(
                selectedLinea = linea,
                direccionActual = 0,
                destino = "Cargando destino..."
            )
        }

        actualizarNombreDestino(linea.id, 0)
        cerrarDialogo()
        cargarDatosPorSentido(linea.id, 0)

        viewModelScope.launch {
            actualizarPosicionesBuses()
        }
    }
}