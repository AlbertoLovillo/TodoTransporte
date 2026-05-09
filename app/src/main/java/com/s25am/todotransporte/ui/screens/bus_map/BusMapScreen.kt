package com.s25am.todotransporte.ui.screens.bus_map

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.s25am.todotransporte.ui.screens.bus_map.components.BusMap
import com.s25am.todotransporte.ui.screens.bus_map.components.MapHeader
import com.s25am.todotransporte.ui.screens.bus_map.components.StopDialog
import com.s25am.todotransporte.ui.screens.bus_map.components.StopsList
import com.s25am.todotransporte.ui.screens.tickets.TicketsViewModel

@SuppressLint("MissingPermission")
@OptIn(MapboxExperimental::class)
@Composable
fun MapsScreen(
    viewModel: BusMapsViewModel = viewModel(),
    ticketsViewModel: TicketsViewModel
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var ubicacionUsuario by remember { mutableStateOf<Location?>(null) }

    val locationRequest = remember {
        LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setMinUpdateIntervalMillis(500L)
            .build()
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                ubicacionUsuario = result.lastLocation
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    ubicacionUsuario = null
                }
            }
        }
    }

    fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            ubicacionUsuario = null
        }
    }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    val lm = context?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                    val isEnabled = lm?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                            lm?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

                    if (isEnabled) {
                        startLocationUpdates()
                    } else {
                        ubicacionUsuario = null
                    }
                }
            }
        }

        context.registerReceiver(receiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))

        onDispose {
            context.unregisterReceiver(receiver)
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    val lanzadorPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permisos ->
            val permisoConcedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (permisoConcedido) {
                startLocationUpdates()
            }
        }
    )

    LaunchedEffect(Unit) {
        lanzadorPermisos.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val uiState by viewModel.uiState.collectAsState()

    val estadoCamara = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(-4.4214, 36.7213))
            zoom(12.0)
            pitch(0.0)
        }
    }

    var mapaListo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000)
        mapaListo = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            if (mapaListo) {
                BusMap(
                    estadoCamara = estadoCamara,
                    lineaSeleccionada = uiState.selectedLinea,
                    rutaGeojson = uiState.rutaGeojsonActual,
                    paradas = uiState.paradas,
                    busesEnTiempoReal = uiState.busesEnTiempoReal,
                    ubicacionUsuario = ubicacionUsuario,
                    onParadaClick = { parada -> viewModel.mostrarInfoParada(parada) }
                )
                MapHeader(//cabecera
                    linea = uiState.selectedLinea,
                    destino = uiState.destino,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        StopsList(
            lineas = uiState.lineas,
            paradas = uiState.paradas,
            lineaSeleccionada = uiState.selectedLinea,
            direccionActual = uiState.direccionActual,
            onAlternarDireccion = { viewModel.alternarDireccion() },
            onSeleccionarLinea = { linea -> viewModel.seleccionarLinea(linea) }
        )

        StopDialog(
            paradaSeleccionada = uiState.paradaSeleccionada,
            lineaSeleccionada = uiState.selectedLinea,
            proximoBusHora = uiState.proximoBusHora,
            onDismiss = { viewModel.cerrarDialogo() }
        )
    }
    // --- Esto de aqui es para Animacion Los tickets ---
    LaunchedEffect(uiState.lineas) {
        if (ticketsViewModel.uiState.value.lineaParaVerEnMapa != null && uiState.lineas.isNotEmpty()) {
            val idBuscado = ticketsViewModel.uiState.value.lineaParaVerEnMapa


            val lineaEncontrada = uiState.lineas.find { it.id.toString() == idBuscado }

            lineaEncontrada?.let { linea ->
                viewModel.seleccionarLinea(linea)
                ticketsViewModel.updateLineaParaVerEnMapa(null)
            }
        }
    }
}