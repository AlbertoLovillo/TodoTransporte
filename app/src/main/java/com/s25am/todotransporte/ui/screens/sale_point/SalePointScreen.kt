package com.s25am.todotransporte.ui.screens.sale_point

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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.s25am.todotransporte.R
import com.s25am.todotransporte.ui.screens.sale_point.components.SalePointsDialog
import com.s25am.todotransporte.ui.screens.sale_point.components.SalePointsMap


@SuppressLint("MissingPermission")
@OptIn(MapboxExperimental::class)
@Composable
fun SalePointScreen(
    viewModel: SalePointViewModel = viewModel()
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var ubicacionUsuario by remember { mutableStateOf<Location?>(null) }

    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setMinUpdateIntervalMillis(500L).build()
    }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                ubicacionUsuario = result.lastLocation
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) ubicacionUsuario = null
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
                    if (isEnabled) startLocationUpdates() else ubicacionUsuario = null
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
            if (permisoConcedido) startLocationUpdates()
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

    SalePointContent(
        uiState = uiState,
        ubicacionUsuario = ubicacionUsuario,
        onPuntoClick = { punto -> viewModel.seleccionarPunto(punto) },
        onCerrarDialogo = { viewModel.cerrarDialogo() }
    )
}

@OptIn(MapboxExperimental::class)
@Composable
fun SalePointContent(
    uiState: SalePointUiState,
    ubicacionUsuario: Location?,
    onPuntoClick: (com.s25am.todotransporte.database.data.PuntoVenta) -> Unit,
    onCerrarDialogo: () -> Unit
) {
    val estadoCamara = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(-4.4214, 36.7213))
            zoom(13.0)
            pitch(0.0)
        }
    }

    var mapaListo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000)
        mapaListo = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (mapaListo) {
            // Le pasamos solo los datos necesarios y el evento click
            SalePointsMap(
                estadoCamara = estadoCamara,
                puntosVenta = uiState.puntosVenta,
                ubicacionUsuario = ubicacionUsuario,
                onPuntoClick = onPuntoClick
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = colorResource(id = R.color.RojoP).copy(alpha = 0.1f),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = colorResource(id = R.color.RojoP),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Puntos de Venta",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${uiState.puntosVenta.size} establecimientos cerca",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }
        }

        // Botón Mi Ubicación
        FloatingActionButton(
            onClick = {
                ubicacionUsuario?.let {
                    estadoCamara.flyTo(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(it.longitude, it.latitude))
                            .zoom(15.0)
                            .build(),
                        MapAnimationOptions.mapAnimationOptions { duration(1000) }
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp),
            containerColor = Color.White,
            contentColor = colorResource(id = R.color.RojoP),
            shape = CircleShape
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
        }

        SalePointsDialog(
            puntoSeleccionado = uiState.puntoSeleccionado,
            onDismiss = onCerrarDialogo
        )
    }
}